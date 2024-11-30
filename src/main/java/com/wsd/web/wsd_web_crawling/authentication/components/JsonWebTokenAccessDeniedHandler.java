package com.wsd.web.wsd_web_crawling.authentication.components;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JsonWebTokenAccessDeniedHandler는 접근이 거부된 경우에 대한 처리를 담당합니다.
 * 이 클래스는 Spring Security의 AccessDeniedHandler 인터페이스를 구현합니다.
 */
@Component
public class JsonWebTokenAccessDeniedHandler implements AccessDeniedHandler {
  /**
   * 접근이 거부된 경우 호출되는 메서드입니다.
   *
   * @param request HttpServletRequest 객체
   * @param response HttpServletResponse 객체
   * @param accessDeniedException 접근 거부 예외
   * @throws IOException 입출력 예외
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
    // 필요한 권한이 없이 접근하려 할때 403
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
  }
}
