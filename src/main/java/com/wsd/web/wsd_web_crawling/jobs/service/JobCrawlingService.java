package com.wsd.web.wsd_web_crawling.jobs.service;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.components.HtmlParser;
import com.wsd.web.wsd_web_crawling.jobs.components.LocationResolver;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsSummary.JobsSummaryRequest;
import com.wsd.web.wsd_web_crawling.common.dto.JobPostingRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobCrawlingService {

  private final JobPostingRepository jobPostingRepository;
  private final LocationResolver locationResolver;

  public void crawlSaramin(JobsSummaryRequest jobsRequest) throws IOException {
    crawlSaramin(jobsRequest, 300);
  }

  public void crawlSaramin(JobsSummaryRequest jobsRequest, int postingCount) throws IOException {
    int requestedPage = postingCount / 6; // 한 페이지 크로링당 6개의 데이터를 가져온다.

    for (int pageParams = 1; pageParams <= requestedPage; pageParams++) {
      String url = "https://www.saramin.co.kr/zf_user/search/recruit?searchType=search"
          + "&searchword=" + jobsRequest.getKeyword()
          + "&recruitPage=" + pageParams
          + "&recruitPageCount=6"
          + "&loc_cd=" + (locationResolver.resolve(jobsRequest.getLoc_cd()) == null ? ""
              : locationResolver.resolve(jobsRequest.getLoc_cd()));
      log.info("url: {}", url);

      Document document = HtmlParser.connectToUrl(url);
      Elements jobElements = HtmlParser.selectElements(document, ".item_recruit");

      log.info("jobElements: {}", jobElements.size());

      // 현재 페이지에 구인 정보가 없으면 종료
      if (jobElements.size() == 0) {
        break;
      }

      for (Element element : jobElements) {
        String title = element.select(".job_tit a").text();
        String company = element.select(".corp_name a").text();
        String link = "https://www.saramin.co.kr" + element.select(".job_tit a").attr("href");
        Elements conditions = element.select(".job_condition span");
        String location = conditions.size() > 0 ? conditions.get(0).text() : "";
        String experience = conditions.size() > 1 ? conditions.get(1).text() : "";
        String education = conditions.size() > 2 ? conditions.get(2).text() : "";
        String employmentType = conditions.size() > 3 ? conditions.get(3).text() : "";
        String deadline = element.select(".job_date .date").text();
        String sector = element.select(".job_sector a").text() + element.select(".job_sector b").text();
        String salary = element.select(".area_badge .badge").text();
        String uniqueIdentifier = title + company;

        // 중복 체크 후 저장
        if (!jobPostingRepository.existsByUniqueIdentifier(uniqueIdentifier)) {
          JobPosting jobPosting = JobPosting.builder()
              .title(title)
              .company(company)
              .link(link)
              .uniqueIdentifier(uniqueIdentifier)
              .location(location)
              .experience(experience)
              .education(education)
              .employmentType(employmentType)
              .deadline(deadline)
              .sector(sector)
              .salary(salary)
              .build();
          jobPostingRepository.save(jobPosting);
        }
      }

      try {
        Thread.sleep(1000); // 서버 부하 방지를 위한 딜레이
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  // 공고 상세 조회
  public JobPosting getJobPostingById(Long id) {
    return jobPostingRepository.findById(id).orElse(null);
  }

  // 조회수 증가
  public void incrementViewCount(Long id) {
    JobPosting jobPosting = jobPostingRepository.findById(id).orElse(null);
    if (jobPosting != null) {
      jobPosting.setViewCount(jobPosting.getViewCount() + 1);
      jobPostingRepository.save(jobPosting);
    }
  }

  // 공고 등록
  public JobPosting createJobPosting(JobPostingRequest request) {
    JobPosting jobPosting = JobPosting.builder()
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
        .viewCount(0)
        .build();
    return jobPostingRepository.save(jobPosting);
  }

  // 공고 수정
  public JobPosting updateJobPosting(Long id, JobPostingRequest request) {
    JobPosting existingJob = jobPostingRepository.findById(id).orElse(null);
    if (existingJob == null) {
      return null;
    }
    existingJob.setTitle(request.getTitle());
    existingJob.setCompany(request.getCompany());
    existingJob.setLink(request.getLink());
    existingJob.setLocation(request.getLocation());
    existingJob.setExperience(request.getExperience());
    existingJob.setEducation(request.getEducation());
    existingJob.setEmploymentType(request.getEmploymentType());
    existingJob.setDeadline(request.getDeadline());
    existingJob.setSector(request.getSector());
    existingJob.setSalary(request.getSalary());
    return jobPostingRepository.save(existingJob);
  }

  // 공고 삭제
  public boolean deleteJobPosting(Long id) {
    if (!jobPostingRepository.existsById(id)) {
      return false;
    }
    jobPostingRepository.deleteById(id);
    return true;
  }
}
