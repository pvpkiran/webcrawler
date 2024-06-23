package at.ecosio.webcrawler.dto;


import java.util.Set;

public record JobResult (int jobId, Status status, Set<String> urls){}
