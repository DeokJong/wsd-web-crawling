package com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobPostingsSummaryResponse extends BaseDto {
    private Long id;
    private String title;
    private String company;
    private String link;
    private String sector;
    private String location;
}

