package at.ecosio.webcrawler.repository;

import at.ecosio.webcrawler.dto.Job;
import at.ecosio.webcrawler.dto.JobResult;
import at.ecosio.webcrawler.dto.Status;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static at.ecosio.webcrawler.dto.Status.COMPLETED;
import static at.ecosio.webcrawler.dto.Status.SUBMITTED;

@Service
public class JobRepository {
    // In memory Data Store
    private final Map<Job, Set<String>> JOBS = new ConcurrentHashMap<>();

    public Job submitUrlForWebcrawler(String url) {
        Job job = new Job(JOBS.size() + 1, url, SUBMITTED);
        JOBS.put(job, Set.of());
        return job;
    }

    public void updateJob(final Job job, final Set<String> urls) {
        job.setStatus(COMPLETED);
        JOBS.put(job, urls);
    }

    public void updateJobStatus(final Job job, final Status status) {
        job.setStatus(status);
    }

    public Optional<JobResult> getJobResult(int jobId) {
        return JOBS.keySet()
                .stream()
                .filter(job -> job.getJobId() == jobId)
                .findFirst()
                .map(job -> {
                    if (job.getStatus() == COMPLETED) {
                        Set<String> urls = JOBS.get(job);
                        return Optional.of(new JobResult(jobId, COMPLETED, urls));
                    } else {
                        return Optional.of(new JobResult(jobId, job.getStatus(), Set.of()));
                    }
                }).orElse(Optional.empty());
    }
}
