package com.wsd.web.wsd_web_crawling.jobs.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailRequest;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JobService는 구인 정보를 크롤링하여 데이터베이스에 저장하고 관리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobService {
    private final JobPostingRepository jobPostingRepository;
    // private final JobCrawlingService jobCrawlingService;

    /**
     * 주어진 요청 조건에 맞는 구인 공고 목록을 조회합니다.
     *
     * @param requestDto 구인 공고 요약 요청 DTO
     * @return 요청 조건에 맞는 구인 공고의 페이지
     */
    public Page<JobPosting> getJobPostings(JobPostingsSummaryRequest requestDto) {
        Pageable pageable = requestDto.toPageable();
        Page<JobPosting> jobPostings = jobPostingRepository.findByKeywordAndLocation(
                requestDto.getKeyword(),
                requestDto.getLocation(),
                pageable);

        return jobPostings;
    }

    /**
     * 지정된 ID를 가진 구인 공고를 조회합니다.
     *
     * @param id 조회할 구인 공고의 ID
     * @return 해당 ID를 가진 구인 공고, 존재하지 않으면 null
     */
    public JobPosting getJobPostingById(Long id) {
        return jobPostingRepository.findById(id).orElse(null);
    }

    /**
     * 지정된 ID의 구인 공고의 조회수를 1 증가시킵니다.
     *
     * @param id 조회수를 증가시킬 구인 공고의 ID
     */
    public void incrementViewCount(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
        if (jobPosting != null) {
            jobPosting.setViewCount(jobPosting.getViewCount() + 1);
            jobPostingRepository.save(jobPosting);
        }
    }

    /**
     * 새로운 구인 공고를 생성하여 저장합니다.
     *
     * @param requestDto 구인 공고 상세 요청 DTO
     * @return 저장된 구인 공고
     * @throws IllegalArgumentException 이미 존재하는 공고일 경우 예외 발생
     */
    public JobPosting createJobPosting(JobPostingDetailRequest requestDto) {
        JobPosting jobPosting = getJobPostingFromRequest(requestDto);

        if (jobPostingRepository.existsByUniqueIdentifier(jobPosting.getUniqueIdentifier())) {
            throw new IllegalArgumentException("이미 존재하는 공고입니다.");
        } else {
            return jobPostingRepository.save(jobPosting);
        }
    }

    /**
     * 지정된 ID의 구인 공고를 삭제합니다.
     *
     * @param id 삭제할 구인 공고의 ID
     * @return 삭제에 성공하면 true, 그렇지 않으면 false
     */
    public boolean deleteJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
        if (jobPosting != null) {
            jobPostingRepository.delete(jobPosting);
            return true;
        }
        return false;
    }

    /**
     * 지정된 ID의 구인 공고를 업데이트합니다.
     *
     * @param id         업데이트할 구인 공고의 ID
     * @param requestDto 구인 공고 상세 업데이트 요청 DTO
     * @return 업데이트된 구인 공고, 존재하지 않으면 null
     */
    public JobPosting updateJobPosting(Long id, JobPostingDetailRequest requestDto) {
        JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
        if (jobPosting != null) {
            jobPosting.updateFrom(requestDto);
            return jobPostingRepository.save(jobPosting);
        }
        return null;
    }

    /**
     * 요청 DTO로부터 JobPosting 객체를 생성합니다.
     *
     * @param requestDto 구인 공고 상세 요청 DTO
     * @return 생성된 JobPosting 객체
     */
    private JobPosting getJobPostingFromRequest(JobPostingDetailRequest requestDto) {
        return JobPosting.builder()
                .title(requestDto.getTitle())
                .company(requestDto.getCompany())
                .link(requestDto.getLink())
                .uniqueIdentifier(requestDto.getTitle() + requestDto.getCompany())
                .location(requestDto.getLocation())
                .experience(requestDto.getExperience())
                .education(requestDto.getEducation())
                .employmentType(requestDto.getEmploymentType())
                .deadline(requestDto.getDeadline())
                .sector(requestDto.getSector())
                .salary(requestDto.getSalary())
                .build();
    }
}
