package com.wsd.web.wsd_web_crawling.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.common.dto.Response;

/**
 * TestController는 다양한 테스트 엔드포인트를 제공하는 REST 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api")
public class TestController {
  /**
   * 공개 테스트 엔드포인트입니다.
   * @return "Public Test" 문자열을 반환합니다.
   */
  @GetMapping("/public/test")
  public ResponseEntity<Response<?>> test() {
    return ResponseEntity.ok(Response.createResponseWithoutData(HttpStatus.OK.value(), "Public Test"));
  }

  /**
   * 사용자 테스트 엔드포인트입니다.
   * @return "User Test" 문자열을 반환합니다.
   */
  @GetMapping("/user/test")
  public ResponseEntity<Response<?>> test2() {
    return ResponseEntity.ok(Response.createResponseWithoutData(HttpStatus.OK.value(), "User Test"));
  }

  /**
   * 관리자 테스트 엔드포인트입니다.
   * @return "Admin Test" 문자열을 반환합니다.
   */
  @GetMapping("/admin/test")
  public ResponseEntity<Response<?>> test3() {
    return ResponseEntity.ok(Response.createResponseWithoutData(HttpStatus.OK.value(), "Admin Test"));
  }
}
