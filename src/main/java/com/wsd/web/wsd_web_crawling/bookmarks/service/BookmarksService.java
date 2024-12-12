package com.wsd.web.wsd_web_crawling.bookmarks.service;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;
import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.BookmarkRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;

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
  public Bookmark readBookmark(HttpServletRequest request) {
    return getBookmarkByRequest(request);
  }

  @Transactional
  public Bookmark addPostingIntoBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    Set<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.add(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    return bookmarkRepository.save(bookmark);
  }

  @Transactional
  public Bookmark removePostingFromBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    Set<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.remove(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    return bookmarkRepository.save(bookmark);
  }

  @Transactional
  public Bookmark getBookmarkByRequest(HttpServletRequest request) {
    String username = tokenProvider.getUsernameFromRequest(request).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.")
    );

    Account account = accountRepository.findByUsername(username).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다.")
    );

    Bookmark bookmark = bookmarkRepository.findByAccountId(account.getId()).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "북마크를 찾을 수 없습니다.")
    );
    log.info("account: {}", account);
    log.info("bookmark: {}", bookmark);
    return bookmark;
  }
}


