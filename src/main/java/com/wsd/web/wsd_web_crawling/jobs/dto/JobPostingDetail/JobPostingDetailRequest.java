package com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class JobPostingDetailRequest extends BaseDto {
  @Schema(description = "제목", example = "Java 개발자")
  private String title;
  @Schema(description = "회사명", example = "원스토어")
  private String company;
  @Schema(description = "링크", example = "https://www.wsd.com/jobs/1234567890")
  private String link;
  @Schema(description = "지역", example = "서울")
  private String location;
  @Schema(description = "경력", example = "3년 이상")
  private String experience;
  @Schema(description = "학력", example = "대학원 졸업")
  private String education;
  @Schema(description = "고용형태", example = "정규직")
  private String employmentType;
  @Schema(description = "마감일", example = "2024-01-01")
  private String deadline;
  @Schema(description = "업종", example = "IT")
  private String sector;
  @Schema(description = "급여", example = "5000만원")
  private String salary;
}