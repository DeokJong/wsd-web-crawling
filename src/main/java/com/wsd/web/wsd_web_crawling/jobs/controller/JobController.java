package com.wsd.web.wsd_web_crawling.jobs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsRequest;
import com.wsd.web.wsd_web_crawling.jobs.service.JobCrawlingService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

// - 채용 공고 API (/jobs)

//   - 공고 목록 조회 (GET /jobs) []
//     - 페이지네이션 처리 (필수) []
//     - 페이지 크기: 20 []
//     - 정렬 기준 제공[]

//   - 필터링 기능 (필수):
//     - 지역별[] // location
//     - 경력별[] // education
//     - 급여별[]
//     - 기술스택별[] // sector 

//   - 검색 기능 (필수):
//     - 키워드 검색[]
//     - 회사명 검색[] // company
//     - 포지션 검색[]

//   - 공고 상세 조회 (GET /jobs/:id)
//     - 상세 정보 제공 []
//     - 조회수 증가[]
//     - 관련 공고 추천[]

// - 채용 공고 관련 API
// - 채용 공고 조회 API
// - 채용 공고 검색 API
// - 채용 공고 필터링 API
// - 채용 공고 정렬 API
// - 채용 공고 등록 API
// - 채용 공고 수정 API
// - 채용 공고 삭제 API

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobCrawlingService jobCrawlingService;

  // 공고 목록 조회
  @GetMapping
  public ResponseEntity<Response<?>> getJobs(@RequestBody JobsRequest jobsRequest) {
    try {
      List<JobPosting> jobPostings = jobCrawlingService.crawlSaramin(jobsRequest); // 크롤링 메서드 호출
      log.info("jobPostings: {}", jobPostings);

      Response<List<JobPosting>> body = Response.createResponse(
          HttpStatus.OK.value(),
          "채용 공고 조회 성공",
          jobPostings
      );

      return new ResponseEntity<>(body, HttpStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
      Response<?> body = Response.createResponseWithoutData(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          "채용 공고 조회 실패"
      );
      return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 공고 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<Response<?>> getJob(@PathVariable Long id) {
    Response<?> body = Response.createResponseWithoutData(
        HttpStatus.OK.value(),
        "채용 공고 상세 조회 성공");

    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
