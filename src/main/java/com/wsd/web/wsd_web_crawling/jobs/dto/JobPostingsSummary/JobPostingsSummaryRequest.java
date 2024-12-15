package com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Builder.Default;

import com.wsd.web.wsd_web_crawling.common.domain.base.BasePageableDto;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobPostingsSummaryRequest extends BasePageableDto {
  @Schema(description = "키워드", example = "Java")
  private String keyword;
  @Default
  @Schema(description = "지역", example = "서울")
  private String location = "";
}
