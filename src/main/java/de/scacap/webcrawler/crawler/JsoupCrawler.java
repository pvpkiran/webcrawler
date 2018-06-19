package de.scacap.webcrawler.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 *  Jsoup based web crawling.
 */
@Component
public class JsoupCrawler implements LinkCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsoupCrawler.class);

  @Override
  public Optional<Document> getPage(String url) {
    LOGGER.debug("Parsing {}", url);
    try {
      return Optional.of(Jsoup.connect(url).get());
    } catch (Exception e) {
      LOGGER.warn("Error crawling {}. Possible reason : page is secured(https).", url);
    }
    return Optional.empty();
  }
}
