package com.wsd.web.wsd_web_crawling.jobs.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary.JobsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary.JobsSummaryResponse;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobRequest;
import com.wsd.web.wsd_web_crawling.jobs.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  @GetMapping("/")
  public ResponseEntity<Response<?>> getJobsSummary(JobsSummaryRequest jobsRequest) {
    Page<JobPosting> jobs = jobService.getJobPostings(jobsRequest);
    Page<JobsSummaryResponse> jobsSummaryResponses = jobs.map(jobPosting -> JobsSummaryResponse.from(jobPosting));
    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "채용 공고 요약 조회 성공", jobsSummaryResponses));
  }

  // 공고 상세 조회 (GET /jobs/{id})
  @GetMapping("/{id}")
  public ResponseEntity<Response<?>> getJobsDetail(@PathVariable Long id) {
    jobService.incrementViewCount(id);
    JobPosting jobPosting = jobService.getJobPostingById(id);
    if (jobPosting == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }
    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "공고 상세 조회 성공", jobPosting));
  }

  // 공고 등록 (POST /jobs)
  @PostMapping("/")
  public ResponseEntity<Response<?>> createJobPosting(@RequestBody JobRequest request) {
    JobPosting createdJob = jobService.createJobPosting(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Response.createResponse(HttpStatus.CREATED.value(), "공고 등록 성공", createdJob));
  }

  // 공고 수정 (PUT /jobs/{id})
  @PutMapping("/{id}")
  public ResponseEntity<Response<?>> updateJobPosting(@PathVariable Long id, @RequestBody JobRequest request) {
    JobPosting updatedJob = jobService.updateJobPosting(id, request);
    if (updatedJob == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }
    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "공고 수정 성공", updatedJob));
  }

  // 공고 삭제 (DELETE /jobs/{id})
  @DeleteMapping("/{id}")
  public ResponseEntity<Response<?>> deleteJobPosting(@PathVariable Long id) {
    boolean isDeleted = jobService.deleteJobPosting(id);
    if (!isDeleted) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }
    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "공고 삭제 성공", null));
  }
}

