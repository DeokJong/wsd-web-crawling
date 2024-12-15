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
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailRequest;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailResponse;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryResponse;
import com.wsd.web.wsd_web_crawling.jobs.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 채용 공고 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  /**
   * 채용 공고의 요약 정보를 조회합니다.
   *
   * @param requestDto 채용 공고 요약 요청 DTO
   * @return ResponseEntity<Response<?>> 채용 공고 요약 조회 결과
   */
  @GetMapping("/")
  public ResponseEntity<Response<?>> getJobsSummary(JobPostingsSummaryRequest requestDto) {
      Page<JobPosting> jobs = jobService.getJobPostings(requestDto);
  
      Page<JobPostingsSummaryResponse> body = jobs.map(jobPosting -> {
          JobPostingsSummaryResponse response = new JobPostingsSummaryResponse();
          response.updateFrom(jobPosting);
          return response;
      });
  
      return ResponseEntity.ok(
          Response.createResponse(HttpStatus.OK.value(), "채용 공고 요약 조회 성공", body)
      );
  }

  /**
   * 특정 공고의 상세 정보를 조회합니다.
   *
   * @param id 조회할 공고의 ID
   * @return ResponseEntity<Response<?>> 공고 상세 조회 결과
   */
  @GetMapping("/{id}")
  public ResponseEntity<Response<?>> getJobsDetail(@PathVariable("id") Long id) {
    jobService.incrementViewCount(id);
    JobPosting jobPosting = jobService.getJobPostingById(id);

    if (jobPosting == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }

    JobPostingDetailResponse body = new JobPostingDetailResponse();
    body.updateFrom(jobPosting);
    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "공고 상세 조회 성공", body));
  }

  /**
   * 새로운 채용 공고를 등록합니다.
   *
   * @param requestDto 등록할 채용 공고의 상세 정보 DTO
   * @return ResponseEntity<Response<?>> 공고 등록 결과
   */
  @PostMapping("/")
  public ResponseEntity<Response<?>> createJobPosting(@RequestBody JobPostingDetailRequest requestDto) {
    JobPosting createdJob = jobService.createJobPosting(requestDto);

    JobPostingDetailResponse body = new JobPostingDetailResponse();
    body.updateFrom(createdJob);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Response.createResponse(HttpStatus.CREATED.value(), "공고 등록 성공", body));
  }

  /**
   * 기존 채용 공고를 수정합니다.
   *
   * @param id 수정할 공고의 ID
   * @param requestDto 수정할 채용 공고의 상세 정보 DTO
   * @return ResponseEntity<Response<?>> 공고 수정 결과
   */
  @PutMapping("/{id}")
  public ResponseEntity<Response<?>> updateJobPosting(@PathVariable("id") Long id, @RequestBody JobPostingDetailRequest requestDto) {
    JobPosting updatedJob = jobService.updateJobPosting(id, requestDto);
    if (updatedJob == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }

    JobPostingDetailResponse body = new JobPostingDetailResponse();
    body.updateFrom(updatedJob);

    return ResponseEntity.ok(Response.createResponse(HttpStatus.OK.value(), "공고 수정 성공", body));
  }

  /**
   * 특정 채용 공고를 삭제합니다.
   *
   * @param id 삭제할 공고의 ID
   * @return ResponseEntity<Response<?>> 공고 삭제 결과
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Response<?>> deleteJobPosting(@PathVariable("id") Long id) {
    boolean isDeleted = jobService.deleteJobPosting(id);
    if (!isDeleted) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Response.createResponse(HttpStatus.NOT_FOUND.value(), "공고를 찾을 수 없습니다.", null));
    }
    return ResponseEntity.ok(Response.createResponse(HttpStatus.NO_CONTENT.value(), "공고 삭제 성공", null));
  }
}

