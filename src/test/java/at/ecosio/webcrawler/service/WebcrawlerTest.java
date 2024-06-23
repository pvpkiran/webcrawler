package at.ecosio.webcrawler.service;

import at.ecosio.webcrawler.dto.Job;
import at.ecosio.webcrawler.dto.JobResult;
import at.ecosio.webcrawler.dto.Status;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static at.ecosio.webcrawler.dto.Status.SUBMITTED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WebcrawlerTest {

    @Autowired
    Webcrawler webcrawler;

    @Autowired
    WebcrawlerService webcrawlerService;


    @ParameterizedTest
    @MethodSource("urlAndBaseDomains")
    void extractDomain() throws MalformedURLException, URISyntaxException {
        String baseDomain = webcrawler.extractDomain("http://www.example.com");
        assertEquals("example.com", baseDomain);
    }

    public static Stream<Arguments> urlAndBaseDomains() {
        return Stream.of(
                Arguments.of("http://www.example.com", "example.com"),
                Arguments.of("https://www.example.com", "example.com"),
                Arguments.of("http://www.example.com/", "example.com"),
                Arguments.of("https://www.example.com/", "example.com"),
                Arguments.of("http://www.example.com/test", "example.com"),
                Arguments.of("https://www.example.com/test", "example.com"),
                Arguments.of("http://www.example.com/test/", "example.com"),
                Arguments.of("https://www.example.com/test/", "example.com"),
                Arguments.of("http://www.example.com/test/test", "example.com")
        );
    }

    @Test
    void testCrawlForValidUrl() {
        Job job = whenAUrlIsSubmittedForCrawling("https://www.google.com");
        verifyThatJobIsAccepted(job);
        waitFor(10, TimeUnit.SECONDS);
        verifyThatJobIsCompleted(job);
    }

    @Test
    void testCrawlForInvalidUrl() {
        Job job = whenAUrlIsSubmittedForCrawling("www.google.com");// Must provide http or https
        waitFor(2, TimeUnit.SECONDS);
        verifyThatJobIsFailed(job);
    }

    private Job whenAUrlIsSubmittedForCrawling(String url) {
        return webcrawlerService.submitUrlForWebcrawler(url);
    }

    private void verifyThatJobIsAccepted(Job job) {
        assertEquals(SUBMITTED, job.getStatus());
    }

    @SneakyThrows
    private void waitFor(int time, TimeUnit timeUnit) {
        Thread.sleep(timeUnit.toMillis(time));
    }

    private void verifyThatJobIsCompleted(Job job) {
        JobResult jobResult = webcrawlerService.getJobResult(job.getJobId()).get();
        assertEquals(Status.COMPLETED, jobResult.status());
        assertFalse(jobResult.urls().isEmpty());
    }

    private void verifyThatJobIsFailed(Job job) {
        JobResult jobResult = webcrawlerService.getJobResult(job.getJobId()).get();
        assertEquals(Status.FAILED, jobResult.status());
    }
}