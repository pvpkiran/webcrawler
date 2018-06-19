package de.scacap.webcrawler;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WebcrawlerApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(WebcrawlerApplication.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setLogStartupInfo(false);
    app.run(args);
  }
}
