package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.model.DateOrder;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ApplicationGetRequest extends BaseDto {
  @Schema(description = "지원 상태 (APPLIED, REJECTED, ACCEPTED, INTERVIEW, OFFER, HIRED)", example = "APPLIED")
  private ApplicationStatus status;

  @Schema(description = "날짜 정렬", example = "ASC")
  private DateOrder dateOrder;
}
