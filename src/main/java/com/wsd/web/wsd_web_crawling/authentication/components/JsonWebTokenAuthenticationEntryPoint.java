package com.wsd.web.wsd_web_crawling.authentication.components;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * JSON Web Token 인증 실패 시 처리하는 클래스입니다.
 * 이 클래스는 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 */
@Component
public class JsonWebTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * 인증 실패 시 호출되는 메서드입니다.
   *
   * @param request  HttpServletRequest 객체
   * @param response HttpServletResponse 객체
   * @param authException 인증 예외
   * @throws IOException 입출력 예외
   */
  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
