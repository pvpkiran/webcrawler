package de.scacap.webcrawler.runner;

import de.scacap.webcrawler.handler.SearchHandler;

import com.google.common.base.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Component
public class CommandLineExecutor implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineExecutor.class);

  private final SearchHandler searchHandler;

  @Autowired
  public CommandLineExecutor(final SearchHandler searchHandler) {
    this.searchHandler = searchHandler;
  }

  @Override
  public void run(String... args) {

    final Scanner in = new Scanner(System.in);
    LOGGER.info("Please provide a keyword for search.");
    final String keywordForSearch = in.nextLine();

    Stopwatch stopwatch = Stopwatch.createStarted();
    List<String> topFiveJsLibraries = searchHandler.handleSearchAndGetTop5Libraries(keywordForSearch);
    stopwatch.stop();

    long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
    LOGGER.debug(" Time Elapsed : {} seconds", elapsed);
    LOGGER.info(" {} ", topFiveJsLibraries);
  }
}
