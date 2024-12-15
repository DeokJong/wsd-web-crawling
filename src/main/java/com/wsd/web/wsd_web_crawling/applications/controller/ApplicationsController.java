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

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationsController {

  private final ApplicationsService applicationsService;

  @GetMapping("/")
  public ResponseEntity<Response<?>> getApplications(
    @Valid @ModelAttribute ApplicationGetRequest requestDto,
    HttpServletRequest httpServletRequest
  ) {
    log.info("request: {}", requestDto);
    Response<?> body = applicationsService.getApplications(httpServletRequest, requestDto);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<Response<?>> addApplication(
    @Valid @RequestBody ApplicationPostRequest requestDto,
    HttpServletRequest httpServletRequest
  ){
    log.info("request: {}", requestDto.getPostingId());
    Response<?> body = applicationsService.addApplication(requestDto, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }

  @DeleteMapping("/{application_id}")
  public ResponseEntity<Response<?>> deleteApplication(
    @PathVariable(required = true, name = "application_id") Long applicationId,
    HttpServletRequest httpServletRequest
  ) {
    Response<?> body = applicationsService.deleteApplication(applicationId, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }

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
