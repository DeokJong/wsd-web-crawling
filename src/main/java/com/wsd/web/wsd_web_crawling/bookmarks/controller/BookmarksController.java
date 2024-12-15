package com.wsd.web.wsd_web_crawling.bookmarks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.bookmarks.dto.BookmarksGetRequest;
import com.wsd.web.wsd_web_crawling.bookmarks.service.BookmarksService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 북마크 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {
  private final BookmarksService bookmarksService;

  /**
   * 사용자의 북마크 목록을 조회합니다.
   *
   * @param httpRequest HTTP 요청 정보
   * @param request     북마크 조회 요청 DTO
   * @return 조회 결과를 담은 ResponseEntity 객체
   */
  @GetMapping
  public ResponseEntity<Response<?>> readBookmark(HttpServletRequest httpRequest, BookmarksGetRequest request) {
    // 서비스에서 DTO를 직접 받아옴
    Response<?> response = bookmarksService.readBookmark(httpRequest, request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  /**
   * 특정 공고를 북마크에 추가합니다.
   *
   * @param postId  추가할 공고의 ID
   * @param request HTTP 요청 정보
   * @return 추가 결과를 담은 ResponseEntity 객체
   */
  @PostMapping("/{post_id}")
  public ResponseEntity<Response<?>> addBookmark(
      @PathVariable(name = "post_id", required = true) @Parameter(description = "북마크에 추가할 공고 ID", required = true, example = "32") Long postId,
      HttpServletRequest request) {

    Response<?> response = bookmarksService.addPostingIntoBookmark(postId, request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  /**
   * 특정 공고를 북마크에서 제거합니다.
   *
   * @param postId  제거할 공고의 ID
   * @param request HTTP 요청 정보
   * @return 제거 결과를 담은 ResponseEntity 객체
   */
  @DeleteMapping("/{post_id}")
  public ResponseEntity<Response<?>> removeBookmark(
      @PathVariable(name = "post_id", required = true) @Parameter(description = "북마크에서 제거할 공고 ID", required = true, example = "32") Long postId,
      HttpServletRequest request) {

    Response<?> response = bookmarksService.removePostingFromBookmark(postId, request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

}
