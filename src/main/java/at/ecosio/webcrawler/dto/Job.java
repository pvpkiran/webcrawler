package at.ecosio.webcrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "jobId")
public class Job {
    int jobId;
    String url;
    Status status;
}
