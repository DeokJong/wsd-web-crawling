package com.wsd.web.wsd_web_crawling.jobs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.components.HtmlParser;
import com.wsd.web.wsd_web_crawling.jobs.components.LocationResolver;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary.JobPostingsSummaryRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JobCrawlingService는 잡코리아의 채용 공고를 크롤링하여 데이터베이스에 저장하는 서비스를 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobCrawlingService {

  private final JobPostingRepository jobPostingRepository;
  private final LocationResolver locationResolver;

  private final Executor executor = Executors.newFixedThreadPool(5); // 동시 작업 개수 제한

  /**
   * 잡코리아에서 채용 공고를 비동기적으로 크롤링합니다.
   *
   * @param jobsRequest 채용 공고 요청 정보
   * @param totalCount 총 크롤링할 페이지 수
   * @return 모든 크롤링 작업이 완료될 때까지의 CompletableFuture
   */
  @Async
  @Transactional
  public CompletableFuture<Void> crawlSaramin(JobPostingsSummaryRequest jobsRequest, int totalCount) {

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    int totalPages = totalCount / 6;

    for (int start = 1; start <= totalPages; start += 10) {
      final int currentPage = start;

      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        try {
          crawlSaramin(jobsRequest, currentPage, currentPage + 9).join();
        } catch (IOException | InterruptedException e) {
          log.error("Error crawling pages {}-{}: {}", currentPage, currentPage + 9, e.getMessage());
        }
      }, executor);

      futures.add(future);
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  /**
   * 지정된 페이지 범위 내에서 잡코리아의 채용 공고를 크롤링합니다.
   *
   * @param jobsRequest 채용 공고 요청 정보
   * @param initPageParams 시작 페이지 번호
   * @param endPageParams 종료 페이지 번호
   * @return 모든 페이지 크롤링 작업이 완료될 때까지의 CompletableFuture
   * @throws IOException 입출력 예외
   * @throws InterruptedException 인터럽트 예외
   */
  @Async
  @Transactional
  public CompletableFuture<Void> crawlSaramin(JobPostingsSummaryRequest jobsRequest, int initPageParams, int endPageParams)
      throws IOException, InterruptedException {
        
        for (int pageParams = initPageParams; pageParams <= endPageParams; pageParams++) {
          String url = "https://www.saramin.co.kr/zf_user/search/recruit?searchType=search"
          + "&searchword=" + jobsRequest.getKeyword()
          + "&recruitPage=" + pageParams
          + "&recruitPageCount=6"
          + "&loc_cd=" + (locationResolver.resolve(jobsRequest.getLocation()) == null ? ""
          : locationResolver.resolve(jobsRequest.getLocation()));
          
          Document document = HtmlParser.connectToUrl(url);
          Elements jobElements = HtmlParser.selectElements(document, ".item_recruit");
          
          if (jobElements.size() == 0) {
            log.info("No job postings found on page {}", pageParams);
            break;
          }
          
          processJobElements(jobElements);
          Thread.sleep(1000); // 서버 부하 방지를 위한 딜레이
        }

        log.info("crawlSaramin end: {}, {}", initPageParams, endPageParams);
        
    return CompletableFuture.completedFuture(null);
  }

  /**
   * 크롤링한 잡 요소들을 처리하여 데이터베이스에 저장합니다.
   *
   * @param jobElements 잡 요소들
   */
  @Transactional
  private void processJobElements(Elements jobElements) {
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
  }
}
