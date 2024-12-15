package com.wsd.web.wsd_web_crawling.applications.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet.ApplicationGetRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationPost.ApplicationPostRequest;
import com.wsd.web.wsd_web_crawling.applications.service.ApplicationsService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import jakarta.servlet.http.HttpServletRequest;
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
    @ModelAttribute ApplicationGetRequest requestDto,
    HttpServletRequest httpServletRequest
  ) {
    log.info("request: {}", requestDto);
    Response<?> body = applicationsService.getApplications(requestDto, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<Response<?>> addApplication(
    @RequestBody ApplicationPostRequest requestDto,
    HttpServletRequest httpServletRequest
  ){
    log.info("request: {}", requestDto.getPostingId());
    Response<?> body = applicationsService.addApplication(requestDto, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<?>> deleteApplication(
    @PathVariable Long id,
    HttpServletRequest httpServletRequest
  ) {
    Response<?> body = applicationsService.deleteApplication(id, httpServletRequest);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
