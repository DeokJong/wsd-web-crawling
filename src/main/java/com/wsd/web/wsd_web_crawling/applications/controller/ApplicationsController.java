package com.wsd.web.wsd_web_crawling.applications.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet.ApplicationGetRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationPost.ApplicationPostRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationUpdateRequest.ApplicationUpdateRequest;
import com.wsd.web.wsd_web_crawling.applications.service.ApplicationsService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationsController {

  private final ApplicationsService applicationsService;

  /**
   * 모든 애플리케이션을 조회합니다.
   *
   * @param requestDto 애플리케이션 조회 요청 DTO
   * @param httpServletRequest HTTP 요청 정보
   * @return 조회된 애플리케이션 목록과 상태를 포함한 응답
   */
  @GetMapping("/")
  public ResponseEntity<Response<?>> getApplications(
    @Valid @ModelAttribute ApplicationGetRequest requestDto,
    HttpServletRequest httpServletRequest
  ) {
    log.info("request: {}", requestDto);
    Response<?> body = applicationsService.getApplications(httpServletRequest, requestDto);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  /**
   * 새로운 애플리케이션을 추가합니다.
   *
   * @param requestDto 애플리케이션 추가 요청 DTO
   * @param httpServletRequest HTTP 요청 정보
   * @return 추가된 애플리케이션 정보와 상태를 포함한 응답
   */
  @PostMapping("/")
  public ResponseEntity<Response<?>> addApplication(
    @Valid @RequestBody ApplicationPostRequest requestDto,
    HttpServletRequest httpServletRequest
  ){
    log.info("request: {}", requestDto.getPostingId());
    Response<?> body = applicationsService.addApplication(requestDto, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }

  /**
   * 특정 애플리케이션을 삭제합니다.
   *
   * @param applicationId 삭제할 애플리케이션의 ID
   * @param httpServletRequest HTTP 요청 정보
   * @return 삭제 결과와 상태를 포함한 응답
   */
  @DeleteMapping("/{application_id}")
  public ResponseEntity<Response<?>> deleteApplication(
    @PathVariable(required = true, name = "application_id") Long applicationId,
    HttpServletRequest httpServletRequest
  ) {
    Response<?> body = applicationsService.deleteApplication(applicationId, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }

  /**
   * 특정 애플리케이션을 업데이트합니다.
   *
   * @param applicationId 업데이트할 애플리케이션의 ID
   * @param requestDto 애플리케이션 업데이트 요청 DTO
   * @param httpServletRequest HTTP 요청 정보
   * @return 업데이트된 애플리케이션 정보와 상태를 포함한 응답
   */
  @PutMapping("/{application_id}")
  public ResponseEntity<Response<?>> updateApplication(
    @PathVariable(required = true, name = "application_id")
    @NotNull
    @Min(1)
    @Max(9999999999L)
    @Schema(description = "지원 아이디", example = "1")
    Long applicationId,
    @RequestBody ApplicationUpdateRequest requestDto,
    HttpServletRequest httpServletRequest
  ) {
    Response<?> body = applicationsService.updateApplication(applicationId, requestDto, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }
}
