package at.ecosio.webcrawler.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "webcrawler")
@Data
@NoArgsConstructor
public class WebcrawlerConfiguration {
    int httpConnectionTimeoutInMillis;
    int httpReadTimeoutInMillis;
    int maxDepth;
    int maxThreads;
    boolean isVirtual;
}
