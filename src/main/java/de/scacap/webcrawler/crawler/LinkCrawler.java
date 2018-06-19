package de.scacap.webcrawler.crawler;

import org.jsoup.nodes.Document;

import java.util.Optional;

/**
 * Generic interface for web crawling.
 */
public interface LinkCrawler {
  Optional<Document> getPage(final String url) ;
}
