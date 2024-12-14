package com.wsd.web.wsd_web_crawling.jobs.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.service.JobCrawlingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCrawlingStartup implements CommandLineRunner {

    private final JobCrawlingService jobCrawlingService;
    private final JobPostingRepository jobPostingsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (jobPostingsRepository.findAll().isEmpty()) {
        JobPostingsSummaryRequest jobsRequest = JobPostingsSummaryRequest.builder()
                .keyword("개발자")
                .location("")
                .build();
            jobCrawlingService.crawlSaramin(jobsRequest, 10);
        } else {
            log.info("Job postings already exist");
        }
    }
}
