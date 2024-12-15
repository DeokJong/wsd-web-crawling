package com.wsd.web.wsd_web_crawling.jobs.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.service.JobCrawlingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JobCrawlingStartup 클래스는 애플리케이션 시작 시 자동으로 실행되어
 * 채용 공고를 크롤링하는 역할을 담당합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobCrawlingStartup implements CommandLineRunner {

    private final JobCrawlingService jobCrawlingService;
    private final JobPostingRepository jobPostingsRepository;

    /**
     * 애플리케이션 시작 시 실행되는 메서드로, 데이터베이스에 채용 공고가
     * 없는 경우 Saramin으로부터 채용 공고를 크롤링합니다.
     *
     * @param args 애플리케이션 시작 시 전달되는 인자들
     * @throws Exception 예외 발생 시 던지는 예외
     */
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
