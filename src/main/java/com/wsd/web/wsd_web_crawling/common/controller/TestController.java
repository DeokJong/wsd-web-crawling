package com.wsd.web.wsd_web_crawling.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController는 다양한 테스트 엔드포인트를 제공하는 REST 컨트롤러입니다.
 */
@RestController
public class TestController {
  /**
   * 공개 테스트 엔드포인트입니다.
   * @return "Public Test" 문자열을 반환합니다.
   */
  @GetMapping("/public/test")
  public String test() {
    return "Public Test";
  }

  /**
   * 사용자 테스트 엔드포인트입니다.
   * @return "User Test" 문자열을 반환합니다.
   */
  @GetMapping("/user/test")
  public String test2() {
    return "User Test";
  }

  /**
   * 관리자 테스트 엔드포인트입니다.
   * @return "Admin Test" 문자열을 반환합니다.
   */
  @GetMapping("/admin/test")
  public String test3() {
    return "Admin Test";
  }
}
