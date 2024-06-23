package at.ecosio.webcrawler.service;

import at.ecosio.webcrawler.config.Timed;
import at.ecosio.webcrawler.config.WebcrawlerConfiguration;
import at.ecosio.webcrawler.dto.Job;
import at.ecosio.webcrawler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static at.ecosio.webcrawler.dto.Status.FAILED;
import static at.ecosio.webcrawler.dto.Status.RUNNING;

@Component
@Slf4j
public class Webcrawler {

    private final WebcrawlerConfiguration webcrawlerConfiguration;
    private final JobRepository jobRepository;
    private final ExecutorService executor;

    public Webcrawler(final WebcrawlerConfiguration webcrawlerConfiguration,
                      final JobRepository jobRepository,
                      final ExecutorService executor) {
        this.webcrawlerConfiguration = webcrawlerConfiguration;
        this.jobRepository = jobRepository;
        this.executor = executor;
    }

    @Async
    @Timed
    public void crawl(final Job job) {
        final Set<String> initialLinksSet = new TreeSet<>();
        initialLinksSet.add(job.getUrl());

        String baseDomain = null;
        try {
            baseDomain = extractDomain(job.getUrl());
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            log.error("Error while extracting base domain. Link: {}. Exception: {}", job.getUrl(), e.getMessage());
            jobRepository.updateJobStatus(job, FAILED);
            return;
        }
        jobRepository.updateJobStatus(job, RUNNING);
        final Set<String> finalLinksSet = retrieveAllLinksFromSite(1, initialLinksSet, baseDomain);

        log.debug("Final link set: for {} is {}", job.getUrl(), finalLinksSet);
        jobRepository.updateJob(job, finalLinksSet);
    }

    private Set<String> retrieveAllLinksFromSite(int level, final Set<String> links, String baseDomain) {
        if (level > webcrawlerConfiguration.getMaxDepth()) {
            return links;
        }

        final Set<String> localLinks = new TreeSet<>();
        final Set<Future<Set<String>>> futures = links.stream()
                .map(link -> executor.submit(() -> {
                    final Set<String> resultLinks = new TreeSet<>();
                    getWebPageAndExtractLinks(baseDomain, link, resultLinks);
                    return resultLinks;
                })).collect(Collectors.toSet());

        futures.forEach(future -> {
            try {
                localLinks.addAll(future.get());
            } catch (Exception e) {
                log.error("Error getting result from futures. Error: {}", e.getMessage());
            }
        });

        localLinks.addAll(retrieveAllLinksFromSite(level + 1, localLinks, baseDomain));
        return localLinks;
    }

    private void getWebPageAndExtractLinks(String baseDomain, String link, final Set<String> resultLinks) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URI(link).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(webcrawlerConfiguration.getHttpConnectionTimeoutInMillis());
            connection.setReadTimeout(webcrawlerConfiguration.getHttpReadTimeoutInMillis());

            StringBuilder content;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
            connection.disconnect();

            String htmlContent = content.toString();
            final Set<String> newLinks = extractLinksFromHtml(htmlContent, baseDomain);
            resultLinks.addAll(newLinks);
        } catch (Exception e) {
            log.error("Error connecting to {}, error: {}", link, e.getMessage());
        }
    }

    private Set<String> extractLinksFromHtml(String htmlContent, String baseDomain) {
        final Set<String> links = new HashSet<>();
        final Pattern pattern = Pattern.compile("href=\"(.*?)\"");
        final Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            String link = matcher.group(1);
            if (isValidLink(link, baseDomain)) {
                links.add(link);
            }
        }
        return links;
    }

    //Excluding all media types as we cannot crawl this further
    private boolean isValidLink(String link, String baseDomain) {
        String[] excludedExtensions = {".png", ".jpg", ".jpeg", ".gif", ".bmp", ".svg", ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
        if (link.startsWith("http") && link.contains(baseDomain)) {
            return Arrays.stream(excludedExtensions)
                    .noneMatch(extension -> link.toLowerCase().endsWith(extension));
        }
        return false;
    }

    //extracting the basedomain given an url. Handles cases with or without the www prefix.
    // Must contain http or https prefix.
    public String extractDomain(String urlString) throws MalformedURLException, URISyntaxException {
        final URL url = new URI(urlString).toURL();
        String domain = url.getHost();

        if (domain.startsWith("www.")) {
            domain = domain.substring(4);
        }

        return domain;
    }
}
