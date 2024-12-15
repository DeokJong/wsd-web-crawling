package com.wsd.web.wsd_web_crawling.bookmarks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.bookmarks.service.BookmarksService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// - 북마크 API (/bookmarks)

//   - 북마크 추가/제거 (POST /bookmarks) []
//     - 인증 확인 []
//     - 북마크 토글 처리 []
//     - 사용자별 저장 []

//   - 북마크 목록 조회 (GET /bookmarks)
//     - 사용자별 북마크 []
//     - 페이지네이션 []
//     - 최신순 정렬 []

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {
  private final BookmarksService bookmarksService;

  @GetMapping
  public ResponseEntity<Response<?>> readBookmark(HttpServletRequest request) {
    // Service에서 DTO를 직접 받아옴
    Response<?> response = bookmarksService.readBookmark(request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  @PostMapping("/{post_id}")
  public ResponseEntity<Response<?>> addBookmark(
      @PathVariable("post_id") @Parameter(description = "북마크에 추가할 공고 ID", required = true, example = "32") Long postId,
      HttpServletRequest request) {

    Response<?> response = bookmarksService.addPostingIntoBookmark(postId, request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  @DeleteMapping("/{post_id}")
  public ResponseEntity<Response<?>> removeBookmark(
      @PathVariable("post_id") @Parameter(description = "북마크에서 제거할 공고 ID", required = true, example = "32") Long postId,
      HttpServletRequest request) {

    Response<?> response = bookmarksService.removePostingFromBookmark(postId, request);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

}
