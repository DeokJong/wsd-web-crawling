package com.wsd.web.wsd_web_crawling.applications.dto;

import java.time.LocalDateTime;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ApplicationDTO extends BaseDto {
  private Long applicationId;
  private String status;
  private Long jobPostingId;
  private String jobPostingTitle;
  private String jobPostingCompany;
  private String jobPostingLink;
  private LocalDateTime appliedAt;
}
