package com.wsd.web.wsd_web_crawling.authentication.components;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * JSON Web Token 인증 실패 시 처리하는 클래스입니다.
 * 이 클래스는 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 */
@Component
public class JsonWebTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  // ObjectMapper를 주입받는 생성자
  public JsonWebTokenAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * 인증 실패 시 호출되는 메서드입니다.
   *
   * @param request       HttpServletRequest 객체
   * @param response      HttpServletResponse 객체
   * @param authException 인증 예외
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    // 상태 코드: 401 Unauthorized
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    // 응답 객체 생성
    Response<?> errorResponse = Response.createResponseWithoutData(
        HttpServletResponse.SC_UNAUTHORIZED,
        "유효한 자격증명을 제공하지 않았습니다.");

    // 객체를 JSON으로 변환 후 전송
    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
  }
}
