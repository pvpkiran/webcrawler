package de.scacap.webcrawler.handler;

import de.scacap.webcrawler.crawler.LinkCrawler;
import de.scacap.webcrawler.pojo.Item;
import de.scacap.webcrawler.pojo.RestResponse;

import com.google.common.collect.ImmutableMap;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "google.custom.search")
public class RestTemplateSearchHandler implements SearchHandler{

  private final RestTemplate restTemplate;
  private final LinkCrawler jsoupCrawler;

  private String baseUrl;
  private String apiKey;
  private String engineId;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getEngineId() {
    return engineId;
  }

  public void setEngineId(String engineId) {
    this.engineId = engineId;
  }

  @Autowired
  public RestTemplateSearchHandler(final RestTemplate restTemplate, final LinkCrawler jsoupCrawler) {
    this.restTemplate = restTemplate;
    this.jsoupCrawler = jsoupCrawler;
  }

  @Override
  public List<String> handleSearchAndGetTop5Libraries(final String keyword) {
    final Map<String, Integer> jsLibraries = new HashMap<>();

    final String createdUrl = getUriComponents(keyword);
    final ResponseEntity<RestResponse> responseEntity = restTemplate.getForEntity(createdUrl, RestResponse.class);

    final RestResponse restResponse = responseEntity.getBody();
    if (restResponse != null) {
      restResponse.getItems()
          .parallelStream()  // Calling in parallel, Since each call takes time.
          .map(Item::getLink)
          .map(jsoupCrawler::getPage)
          .collect(Collectors.toSet()) // Doing collect before further processing to ensure thread safety.
          .forEach(getLibrariesFromPage(jsLibraries));
    }

    return sortByValues(jsLibraries)
        .keySet()
        .stream()
        .limit(5)  // Getting only top 5
        .collect(Collectors.toList());
  }

  private Consumer<Optional<Document>> getLibrariesFromPage(final Map<String, Integer> jsLibraries) {
    return optionalDocument -> optionalDocument.ifPresent(document -> {
      final Elements scripts = document.getElementsByTag("script");
      scripts.forEach(script -> {
        final String src = script.attributes().get("src");
        if (!src.isEmpty() && !script.attributes().get("type").isEmpty())
          addToMap(jsLibraries, handleDuplication(src.substring(src.lastIndexOf("/") + 1)));
      });
    });
  }

  private String getUriComponents(final String keyword) {
    final Map<String, String> queryParams = ImmutableMap.of(
        "key", apiKey,
        "cx", engineId,
        "q", keyword);

    return UriComponentsBuilder
        .fromHttpUrl(baseUrl)
        .buildAndExpand(queryParams)
        .toUriString();
  }
}
