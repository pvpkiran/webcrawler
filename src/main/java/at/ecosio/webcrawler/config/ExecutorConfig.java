package at.ecosio.webcrawler.config;

import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class ExecutorConfig {

    private ExecutorService executor;

    @SneakyThrows
    @Bean
    public ExecutorService taskExecutor(final WebcrawlerConfiguration webcrawlerConfiguration) {
        if(webcrawlerConfiguration.isVirtual()) {
            executor = Executors.newVirtualThreadPerTaskExecutor();
            return executor;
        }
        executor = Executors.newFixedThreadPool(webcrawlerConfiguration.getMaxThreads());
        executor.awaitTermination(20, SECONDS);
        return executor;
    }

    @PreDestroy
    public void shutDown() {
        executor.shutdown();
    }
}
