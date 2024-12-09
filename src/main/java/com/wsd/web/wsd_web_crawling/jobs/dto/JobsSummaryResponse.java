package com.wsd.web.wsd_web_crawling.jobs.dto;

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
}
