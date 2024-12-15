package com.wsd.web.wsd_web_crawling.authentication.components;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 인증 필터 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter {

  @Value("${jwt.authorization-header-access}")
  private String AUTHORIZATION_HEADER_ACCESS;
  private final JsonWebTokenProvider tokenProvider;

  // 필터를 제외할 경로 설정
  private static final List<String> EXCLUDED_PATHS = List.of(
      "/api/auth/**",
      "/api/sign-up",
      "/api/public/**",
      "/swagger-ui/**",
      "/swagger-ui.html",
      "/v3/api-docs/**",
      "/swagger-resources/**");
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  /**
   * 특정 경로에 대해 필터링 제외
   *
   * @param request HTTP 요청
   * @return 필터링 제외 여부
   */
  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String requestPath = request.getRequestURI();
    return EXCLUDED_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
  }

  /**
   * Request에서 토큰 추출
   *
   * @param request HTTP 요청
   * @return 추출된 JWT 토큰 문자열 또는 null
   */
  private String resolveToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (AUTHORIZATION_HEADER_ACCESS.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }

  /**
   * 필터링 로직 구현
   *
   * @param request     HTTP 요청
   * @param response    HTTP 응답
   * @param filterChain 필터 체인
   * @throws ServletException 서블릿 예외
   * @throws IOException      I/O 예외
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      // 요청에서 토큰 추출
      String token = resolveToken(request);
      log.debug("Extracted JWT token: {}", token);
      // 토큰 유효성 검증 및 인증 객체 설정
      if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        // SecurityContextHolder에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(),
            request.getRequestURI());
      }
    } catch (AuthenticationException e) {
      SecurityContextHolder.clearContext();
    }

    // 필터 체인 계속 진행
    filterChain.doFilter(request, response);
  }
}
