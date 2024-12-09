package com.wsd.web.wsd_web_crawling.jobs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.components.HtmlParser;
import com.wsd.web.wsd_web_crawling.jobs.components.JsonResolver;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobsRequest;

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
public class JobCrawlingService {

  private final JobPostingRepository jobPostingRepository;
  private final JsonResolver locationResolver;

  public List<JobPosting> crawlSaramin(JobsRequest jobsRequest) throws IOException {
    // 페이지당 데이터 크기
    int pageSize = 20;

    // 요청된 페이지 번호 (1부터 시작하도록 보정)
    int requestedPage = Math.max(jobsRequest.getPage(), 1) - 1;

    // 1. 기존 데이터를 가져오고 20개씩 묶음
    List<JobPosting> existingJobPostings = jobPostingRepository.findByKeywordInSector(jobsRequest.getKeyword());
    List<List<JobPosting>> paginatedJobPostings = paginate(existingJobPostings, pageSize);

    // 2. 요청된 페이지 데이터가 존재하면 반환
    if (requestedPage < paginatedJobPostings.size()) {
      return paginatedJobPostings.get(requestedPage);
    }

    // 3. 크롤링 시작 - 부족한 데이터 채우기
    int requiredDataCount = (requestedPage + 1) * pageSize; // 요청된 페이지까지 필요한 데이터 수
    for (int pageParams = 1; existingJobPostings.size() < requiredDataCount; pageParams++) {
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
          existingJobPostings.add(jobPosting);
        }
        if (existingJobPostings.size() >= requiredDataCount) {
          break;
        }
      }

      try {
        Thread.sleep(1000); // 서버 부하 방지를 위한 딜레이
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    // 4. 업데이트된 데이터를 기반으로 페이지네이션
    paginatedJobPostings = paginate(existingJobPostings, pageSize);

    // 5. 요청된 페이지 데이터 반환
    return requestedPage < paginatedJobPostings.size() ? paginatedJobPostings.get(requestedPage) : new ArrayList<>();
  }

  public Optional<JobPosting> crawlSaraminDetail(Long id) throws IOException {
    return jobPostingRepository.findById(id);
  }

  /**
   * 데이터를 주어진 크기 단위로 묶는 메서드
   */
  private List<List<JobPosting>> paginate(List<JobPosting> jobPostings, int pageSize) {
    List<List<JobPosting>> paginated = new ArrayList<>();
    for (int i = 0; i < jobPostings.size(); i += pageSize) {
      int end = Math.min(i + pageSize, jobPostings.size());
      paginated.add(jobPostings.subList(i, end));
    }
    return paginated;
  }
}
