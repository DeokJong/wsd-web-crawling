package com.wsd.web.wsd_web_crawling.jobs.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary.JobsSummaryRequest;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JobCrawlingService는 구인 정보를 크롤링하여 데이터베이스에 저장하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobService {
  private final JobPostingRepository jobPostingRepository;
  // private final JobCrawlingService jobCrawlingService;

  public Page<JobPosting> getJobPostings(JobsSummaryRequest jobsRequest) {
    Pageable pageable = jobsRequest.toPageable();
    Page<JobPosting> jobPostings = jobPostingRepository.findByKeywordAndLocation(
        jobsRequest.getKeyword(),
        jobsRequest.getLocation(),
        pageable);
    if (jobPostings.getTotalElements() < 100) {
      log.info("crawling start but not implemented");
      // jobCrawlingService.crawlSaramin(jobsRequest, 10);
    }

    return jobPostings;
  }

  public JobPosting getJobPostingById(Long id) {
    return jobPostingRepository.findById(id).orElse(null);
  }

  public void incrementViewCount(Long id) {
    JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
    if (jobPosting != null) {
      jobPosting.setViewCount(jobPosting.getViewCount() + 1);
      jobPostingRepository.save(jobPosting);
    }
  }

  public JobPosting createJobPosting(JobRequest request) {
    JobPosting jobPosting = getJobPostingFromRequest(request);

    if (jobPostingRepository.existsByUniqueIdentifier(jobPosting.getUniqueIdentifier())) {
      throw new IllegalArgumentException("이미 존재하는 공고입니다.");
    } else {
      return jobPostingRepository.save(jobPosting);
    }
  }

  public boolean deleteJobPosting(Long id) {
    JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
    if (jobPosting != null) {
      jobPostingRepository.delete(jobPosting);
      return true;
    }
    return false;
  }

  public JobPosting updateJobPosting(Long id, JobRequest request) {
    JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
    if (jobPosting != null) {
      jobPosting.update(request);
      return jobPostingRepository.save(jobPosting);
    }
    return null;
  }

  private JobPosting getJobPostingFromRequest(JobRequest request) {
    return JobPosting.builder()
        .title(request.getTitle())
        .company(request.getCompany())
        .link(request.getLink())
        .uniqueIdentifier(request.getTitle() + request.getCompany())
        .location(request.getLocation())
        .experience(request.getExperience())
        .education(request.getEducation())
        .employmentType(request.getEmploymentType())
        .deadline(request.getDeadline())
        .sector(request.getSector())
        .salary(request.getSalary())
        .build();
  }
}
