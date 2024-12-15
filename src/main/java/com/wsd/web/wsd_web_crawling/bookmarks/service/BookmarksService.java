package com.wsd.web.wsd_web_crawling.bookmarks.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.bookmarks.dto.BookmarksResponse;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;
import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.BookmarkRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarksService {
  private final AccountRepository accountRepository;
  private final BookmarkRepository bookmarkRepository;
  private final JobPostingRepository jobPostingRepository;
  private final JsonWebTokenProvider tokenProvider;

@Transactional
public BookmarksResponse readBookmark(HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);

    if (bookmark.getJobPostings() == null) {
      return BookmarksResponse.builder()
        .jobPostingDetailResponses(Collections.emptyList())
        .build();
    }

    // 트랜잭션 범위 내에서 jobPostings 접근 및 DTO 변환
    List<JobPostingDetailResponse> jobPostingDetailResponses = bookmark.getJobPostings().stream()
        .map(jobPosting -> {
          JobPostingDetailResponse response = new JobPostingDetailResponse();
          response.updateFrom(jobPosting);
          return response;
        })
        .collect(Collectors.toList());

    // BookmarksResponse DTO 생성 및 데이터 설정
    BookmarksResponse response = new BookmarksResponse();
    response.updateFrom(bookmark);
    response.setJobPostingDetailResponses(jobPostingDetailResponses);

    return response;
}


  @Transactional
  public BookmarksResponse addPostingIntoBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    List<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.add(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    bookmarkRepository.save(bookmark);

    BookmarksResponse response = new BookmarksResponse();
    response.updateFrom(bookmark);
    return response;
  }

  @Transactional
  public BookmarksResponse removePostingFromBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    List<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.remove(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    bookmarkRepository.save(bookmark);

    BookmarksResponse response = new BookmarksResponse();
    response.updateFrom(bookmark);
    return response;
  }

  @Transactional
  public Bookmark getBookmarkByRequest(HttpServletRequest request) {
    String username = tokenProvider.getUsernameFromRequest(request).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.")
    );

    Account account = accountRepository.findByUsername(username).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다.")
    );

    Bookmark bookmark = bookmarkRepository.findByAccountId(account.getId()).orElse(null);

    if (bookmark == null) {
      bookmark = Bookmark.builder()
        .account(account)
        .build();
      bookmarkRepository.save(bookmark);
    }

    return bookmark;
  }
}


