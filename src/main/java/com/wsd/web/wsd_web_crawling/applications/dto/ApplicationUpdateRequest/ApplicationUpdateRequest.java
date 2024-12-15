package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationUpdateRequest;

import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 지원 업데이트 요청을 나타내는 DTO 클래스입니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUpdateRequest {
  
  /**
   * 지원 상태를 나타냅니다. 유효한 값이어야 합니다.
   * 
   * @예시 "APPLIED"
   */
  @NotNull
  @Schema(description = "지원 상태", example = "APPLIED")
  private ApplicationStatus status;
}
