package at.ecosio.webcrawler.service;

import at.ecosio.webcrawler.dto.Job;
import at.ecosio.webcrawler.dto.JobResult;
import at.ecosio.webcrawler.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebcrawlerService {

    private final JobRepository jobRepository;
    private final Webcrawler webcrawler;

    public WebcrawlerService(final JobRepository jobRepository,
                             final Webcrawler webcrawler) {
        this.jobRepository = jobRepository;
        this.webcrawler = webcrawler;
    }

    public Job submitUrlForWebcrawler(String url) {
        Job job = jobRepository.submitUrlForWebcrawler(url);
        webcrawler.crawl(job);
        return job;
    }

    public Optional<JobResult> getJobResult(int jobId) {
      return jobRepository.getJobResult(jobId);
    }
}
