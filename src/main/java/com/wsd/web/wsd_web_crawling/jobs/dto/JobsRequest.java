package com.wsd.web.wsd_web_crawling.jobs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class JobsRequest {
  @Default
  @Schema(description = "페이지 번호", example = "1")
  private int page = 1;
  @Default
  @Schema(description = "페이지 크기", example = "20")
  private int size = 20;
  @Schema(description = "키워드", example = "Java")
  private String keyword;
  @Default
  @Schema(description = "지역", example = "서울")
  private String loc_cd = "";
}