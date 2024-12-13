package com.wsd.web.wsd_web_crawling.applications.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.applications.service.ApplicationsService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import lombok.RequiredArgsConstructor;

// - 지원 관리 API (/applications)

//   - 지원하기 (POST /applications) []
//     - 인증 확인 []
//     - 중복 지원 체크 []
//     - 지원 정보 저장 []
//     - 이력서 첨부 (선택) []

//   - 지원 내역 조회 (GET /applications)
//     - 사용자별 지원 목록 []
//     - 상태별 필터링[]
//     - 날짜별 정렬[]

//   - 지원 취소 (DELETE /applications/:id) []
//     - 인증 확인 []
//     - 취소 가능 여부 확인[]
//     - 상태 업데이트[]

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationsController {

  private final ApplicationsService applicationsService;

  @PostMapping
  public ResponseEntity<Response<?>> addApplication(@RequestParam Long jobPostingId,
      @RequestParam(required = false) String resume) {
    Response<?> body = applicationsService.addApplication(jobPostingId, resume);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<Response<?>> getApplications(@RequestParam(required = false) String status,
      @RequestParam(required = false) String sort) {
    Response<?> body = applicationsService.getApplications(status, sort);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response<?>> deleteApplication(@PathVariable Long id) {
    Response<?> body = applicationsService.deleteApplication(id);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
