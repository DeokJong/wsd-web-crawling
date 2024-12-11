package com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobsSummaryResponse {
    private Long jobId;
    private String title;
    private String company;
    private String link;
    private String sector;
    private String location;

    public static JobsSummaryResponse from(JobPosting jobPosting) {
        return JobsSummaryResponse.builder()
            .jobId(jobPosting.getId())
            .title(jobPosting.getTitle())
            .company(jobPosting.getCompany())
            .link(jobPosting.getLink())
            .sector(jobPosting.getSector())
            .location(jobPosting.getLocation())
            .build();
    }
}

