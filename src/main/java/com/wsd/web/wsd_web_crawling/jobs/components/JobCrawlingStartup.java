package com.wsd.web.wsd_web_crawling.jobs.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary.JobsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.service.JobCrawlingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobCrawlingStartup implements CommandLineRunner {

    private final JobCrawlingService jobCrawlingService;

    @Override
    public void run(String... args) throws Exception {
        JobsSummaryRequest jobsRequest = JobsSummaryRequest.builder()
                .keyword("개발자")
                .location("")
                .build();
        jobCrawlingService.crawlSaramin(jobsRequest, 10);
    }
}
