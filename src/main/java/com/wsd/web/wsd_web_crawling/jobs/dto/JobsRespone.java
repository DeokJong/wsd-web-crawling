package com.wsd.web.wsd_web_crawling.jobs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobsRespone {
    private String title;
    private String company;
    private String link;
    private String salary;
}
