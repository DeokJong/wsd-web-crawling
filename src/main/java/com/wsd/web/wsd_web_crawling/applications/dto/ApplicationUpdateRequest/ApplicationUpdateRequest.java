package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationUpdateRequest;

import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUpdateRequest {
  @NotNull
  @Schema(description = "지원 상태", example = "APPLIED")
  private ApplicationStatus status;
}
