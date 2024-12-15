package com.wsd.web.wsd_web_crawling.bookmarks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.bookmarks.dto.BookmarksGetRequest;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;
import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.BookmarkRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 북마크 관련 서비스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarksService {
  private final AccountRepository accountRepository;
  private final BookmarkRepository bookmarkRepository;
  private final JobPostingRepository jobPostingRepository;
  private final JsonWebTokenProvider tokenProvider;

  /**
   * 사용자의 북마크를 조회합니다.
   *
   * @param httpRequest HTTP 요청 객체
   * @param request     북마크 조회 요청 DTO
   * @return 조회 결과를 포함한 응답 객체
   */
  @Transactional
  public Response<?> readBookmark(HttpServletRequest httpRequest, BookmarksGetRequest request) {
    if (tokenProvider.getAccessTokenFromRequest(httpRequest) == null) {
      return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다.");
    }

    tokenProvider.validateToken(tokenProvider.getAccessTokenFromRequest(httpRequest));

    Bookmark bookmark = getBookmarkByRequest(httpRequest);

    if (bookmark.getJobPostings() == null) {
      return Response.createResponseWithoutData(HttpStatus.OK.value(), "북마크가 비어있습니다.");
    }

    Pageable pageable = request.toPageable();

    List<JobPostingDetailResponse> responses = bookmark.getJobPostings()
        .stream()
        .map(jobPosting -> {
          JobPostingDetailResponse response = new JobPostingDetailResponse();
          response.updateFrom(jobPosting);
          return response;
        })
        .collect(Collectors.toList());

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), responses.size());
    List<JobPostingDetailResponse> pageContent = start > end ? new ArrayList<>() : responses.subList(start, end);

    Page<JobPostingDetailResponse> page = new PageImpl<>(pageContent, pageable, responses.size());

    return Response.createResponse(HttpStatus.OK.value(), "북마크 조회 성공", page);
  }

  /**
   * 특정 잡 포스팅을 사용자의 북마크에 추가합니다.
   *
   * @param jobPostingId 추가할 잡 포스팅의 ID
   * @param request      HTTP 요청 객체
   * @return 추가 결과를 포함한 응답 객체
   */
  @Transactional
  public Response<?> addPostingIntoBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    if (bookmark.getJobPostings().contains(jobPosting)) {
      return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "이미 북마크에 존재하는 잡 포스팅입니다.");
    }

    List<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.add(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    bookmarkRepository.save(bookmark);

    JobPostingDetailResponse response = new JobPostingDetailResponse();
    response.updateFrom(jobPosting);

    return Response.createResponse(HttpStatus.CREATED.value(), "북마크 추가 성공", response);
  }

  /**
   * 특정 잡 포스팅을 사용자의 북마크에서 제거합니다.
   *
   * @param jobPostingId 제거할 잡 포스팅의 ID
   * @param request      HTTP 요청 객체
   * @return 제거 결과를 포함한 응답 객체
   */
  @Transactional
  public Response<?> removePostingFromBookmark(Long jobPostingId, HttpServletRequest request) {
    Bookmark bookmark = getBookmarkByRequest(request);
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "잡 포스팅을 찾을 수 없습니다."));

    List<JobPosting> bookmarkJobPosting = bookmark.getJobPostings();
    bookmarkJobPosting.remove(jobPosting);
    bookmark.setJobPostings(bookmarkJobPosting);
    bookmarkRepository.save(bookmark);

    return Response.createResponseWithoutData(HttpStatus.NO_CONTENT.value(), "북마크 삭제 성공");
  }

  /**
   * 요청으로부터 사용자의 북마크를 조회합니다.
   *
   * @param request HTTP 요청 객체
   * @return 사용자의 북마크 엔티티
   */
  @Transactional
  public Bookmark getBookmarkByRequest(HttpServletRequest request) {
    String username = tokenProvider.getUsernameFromRequest(request).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."));

    Account account = accountRepository.findByUsername(username).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다."));

    Bookmark bookmark = bookmarkRepository.findByAccountId(account.getId()).orElse(null);

    if (bookmark == null) {
      bookmark = Bookmark.builder()
          .account(account)
          .build();
      bookmarkRepository.save(bookmark);
    }

    return bookmark;
  }

  /**
   * 요청으로부터 사용자의 북마크를 페이징 처리하여 조회합니다.
   *
   * @param httpRequest HTTP 요청 객체
   * @param request     북마크 조회 요청 DTO
   * @return 페이징 처리된 북마크 페이지
   */
  @Transactional
  public Page<Bookmark> getBookmarkByRequestWithPageable(HttpServletRequest httpRequest, BookmarksGetRequest request) {
    String username = tokenProvider.getUsernameFromRequest(httpRequest).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."));

    Account account = accountRepository.findByUsername(username).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다."));

    Bookmark bookmark = bookmarkRepository.findByAccountId(account.getId()).orElse(null);

    if (bookmark == null) {
      bookmark = Bookmark.builder()
          .account(account)
          .build();
      bookmarkRepository.save(bookmark);
    }

    return bookmarkRepository.findByAccountId(account.getId(), request.toPageable());
  }
}
