package com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail;

import java.time.LocalDateTime;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobPostingDetailResponse extends BaseDto {
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long id;
  private String title;
  private String company;
  private String link;
  private String uniqueIdentifier;
  private String location;
  private String experience;
  private String education;
  private String employmentType;
  private String deadline;
  private String sector;
  private String salary;
  private Integer viewCount;
}
