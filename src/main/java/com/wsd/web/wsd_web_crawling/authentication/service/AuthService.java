package com.wsd.web.wsd_web_crawling.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.authentication.dto.AccountCreateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.AccountUpdateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.LoginRequest;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.BookmarkRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JsonWebTokenProvider tokenProvider;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final BookmarkRepository bookmarkRepository;

  /**
   * 사용자 로그인 처리.
   *
   * @param loginRequest 로그인 요청 정보
   * @param response     HTTP 응답 객체
   * @throws AuthenticationException 인증 실패 시 발생
   */
  public void login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()));

      String accessToken = tokenProvider.createToken(authentication);
      String refreshToken = tokenProvider.createRefreshToken(authentication);

      response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_ACCESS, accessToken,
          tokenProvider.tokenValidityInMilliseconds));
      response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_REFRESH, refreshToken,
          tokenProvider.refreshTokenValidityInMilliseconds));
    } catch (AuthenticationException e) {
      throw e;
    }
  }

  /**
   * 사용자 로그아웃 처리.
   *
   * @param response HTTP 응답 객체
   */
  public void logout(HttpServletResponse response) {
    tokenProvider.deleteCookie(response, tokenProvider.AUTHORIZATION_HEADER_REFRESH);
    tokenProvider.deleteCookie(response, tokenProvider.AUTHORIZATION_HEADER_ACCESS);
  }

  /**
   * 새로운 사용자 등록.
   *
   * @param request 사용자 생성 요청 정보
   */
  public void register(AccountCreateRequest request) {

    if (accountRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");
    }

    Account account = accountRepository.save(Account.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .build());

    bookmarkRepository.save(Bookmark.builder()
        .account(account)
        .jobPostings(null)
        .build());
  }

  /**
   * 리프레시 토큰을 사용하여 액세스 토큰 갱신.
   *
   * @param request  HTTP 요청 객체
   * @param response HTTP 응답 객체
   */
  public void getAccessTokenRefresh(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    String refreshToken = null;

    for (Cookie cookie : cookies) {
      if (tokenProvider.AUTHORIZATION_HEADER_REFRESH.equals(cookie.getName())) {
        refreshToken = cookie.getValue();
      }
    }

    if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
      String accessToken = tokenProvider.createToken(tokenProvider.getAuthentication(refreshToken));
      response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_ACCESS, accessToken, tokenProvider.tokenValidityInMilliseconds));
    }
  }

  /**
   * 사용자 프로필 업데이트.
   *
   * @param updateRequest 업데이트 요청 정보
   * @param request       HTTP 요청 객체
   * @param response      HTTP 응답 객체
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
   */
  @Transactional
  public void updateProfile(AccountUpdateRequest updateRequest, HttpServletRequest request, HttpServletResponse response) {
    String username = tokenProvider.getUsernameFromRequest(request).orElse(null);
    Account account = accountRepository.findByUsername(username)
        .orElse(null);

    if (account == null) {
      throw new UsernameNotFoundException("User not found");
    }

    if (updateRequest.getPassword() != null) {
      account.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    if (updateRequest.getNickname() != null) {
      account.setNickname(updateRequest.getNickname());
    }

    accountRepository.save(account);
  }
}
