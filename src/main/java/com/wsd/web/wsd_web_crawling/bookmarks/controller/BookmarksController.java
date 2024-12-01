package com.wsd.web.wsd_web_crawling.bookmarks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.common.dto.Response;

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
  @PostMapping
  public ResponseEntity<Response<?>> addBookmark() {
    Response<?> body = Response.createResponseWithoutData(HttpStatus.OK.value(), "북마크 추가 성공");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<Response<?>> getBookmarks() {
    Response<?> body = Response.createResponseWithoutData(HttpStatus.OK.value(), "북마크 목록 조회 성공");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
