package at.ecosio.webcrawler.rest;

import at.ecosio.webcrawler.dto.Job;
import at.ecosio.webcrawler.dto.JobResult;
import at.ecosio.webcrawler.service.WebcrawlerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/crawl")
public class WebcrawlerController {

    private final WebcrawlerService webcrawlerService;

    public WebcrawlerController(WebcrawlerService webcrawlerService) {
        this.webcrawlerService = webcrawlerService;
    }

    @PostMapping("/submit")
    @ResponseStatus(ACCEPTED)
    public Job submitUrlToWebCrawler(String url) {
        return webcrawlerService.submitUrlForWebcrawler(url);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobResult> getJobStatus(@PathVariable int jobId) {
         return webcrawlerService.getJobResult(jobId)
                 .map(ResponseEntity::ok)
                 .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
