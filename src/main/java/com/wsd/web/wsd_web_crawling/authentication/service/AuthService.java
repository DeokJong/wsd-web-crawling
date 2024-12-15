package com.wsd.web.wsd_web_crawling.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.authentication.components.RefreshTokenStore;
import com.wsd.web.wsd_web_crawling.authentication.dto.AccountCreateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.AccountUpdateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.LoginRequest;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.BookmarkRepository;

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
  private final RefreshTokenStore refreshTokenStore;

  /**
   * 사용자 로그인 처리를 담당하는 메서드입니다.
   *
   * @param loginRequest 로그인 요청 정보
   * @param response     HTTP 응답 객체
   * @return 로그인 성공 여부에 대한 응답 객체
   * @throws AuthenticationException 인증 실패 시 발생
   */
  public Response<?> login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()));

      String accessToken = tokenProvider.createToken(authentication);
      String refreshToken = tokenProvider.createRefreshToken(authentication);

      refreshTokenStore.saveRefreshToken(authentication.getName(), refreshToken);

      response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_ACCESS, accessToken,
          tokenProvider.tokenValidityInMilliseconds));
      response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_REFRESH, refreshToken,
          tokenProvider.refreshTokenValidityInMilliseconds));
    } catch (AuthenticationException e) {
      throw e;
    }

    return Response.createResponseWithoutData(HttpStatus.OK.value(), "로그인 성공");
  }

  /**
   * 사용자 로그아웃 처리를 담당하는 메서드입니다.
   *
   * @param request  HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @return 로그아웃 성공 여부에 대한 응답 객체
   */
  public Response<?> logout(HttpServletRequest request, HttpServletResponse response) {

    String refreshToken = tokenProvider.getRefreshTokenFromRequest(request);
    String username = tokenProvider.getUsernameFromRequest(request).orElse(null);
    refreshTokenStore.deleteRefreshToken(username, refreshToken);
    tokenProvider.deleteCookie(response, tokenProvider.AUTHORIZATION_HEADER_REFRESH);
    tokenProvider.deleteCookie(response, tokenProvider.AUTHORIZATION_HEADER_ACCESS);

    return Response.createResponseWithoutData(HttpStatus.OK.value(), "로그아웃 성공");
  }

  /**
   * 새로운 사용자를 등록하는 메서드입니다.
   *
   * @param request 사용자 생성 요청 정보
   * @return 회원가입 성공 여부에 대한 응답 객체
   */
  public Response<?> register(AccountCreateRequest request) {

    if (accountRepository.existsByUsername(request.getUsername())) {
      return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "이미 존재하는 사용자입니다.");
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

    return Response.createResponseWithoutData(HttpStatus.OK.value(), "회원가입 성공");
  }

  /**
   * 리프레시 토큰을 사용하여 액세스 토큰을 갱신하는 메서드입니다.
   *
   * @param request  HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @return 액세스 토큰 갱신 여부에 대한 응답 객체
   */
  public Response<?> getAccessTokenRefresh(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = tokenProvider.getRefreshTokenFromRequest(request);
    String accessToken = tokenProvider.createAccessTokenFromRefreshToken(refreshToken);
    response.addCookie(tokenProvider.createCookie(tokenProvider.AUTHORIZATION_HEADER_ACCESS, accessToken, tokenProvider.tokenValidityInMilliseconds));

    if (accessToken == null) {
      return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "리프레시 토큰이 만료되었습니다.");
    }

    return Response.createResponseWithoutData(HttpStatus.OK.value(), "액세스 토큰이 갱신되었습니다.");
  }

  /**
   * 사용자 프로필을 업데이트하는 메서드입니다.
   *
   * @param updateRequest 업데이트 요청 정보
   * @param request       HTTP 요청 객체
   * @param response      HTTP 응답 객체
   * @return 프로필 업데이트 성공 여부에 대한 응답 객체
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
   */
  @Transactional
  public Response<?> updateProfile(AccountUpdateRequest updateRequest, HttpServletRequest request, HttpServletResponse response) {
    String username = tokenProvider.getUsernameFromRequest(request).orElse(null);
    Account account = accountRepository.findByUsername(username)
        .orElse(null);

    if (account == null) {
      return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.");
    }

    if (updateRequest.getPassword() != null) {
      account.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    if (updateRequest.getNickname() != null) {
      account.setNickname(updateRequest.getNickname());
    }

    accountRepository.save(account);

    return Response.createResponseWithoutData(HttpStatus.OK.value(), "프로필 업데이트 성공");
  }

  /**
   * 현재 인증된 사용자의 계정을 가져오는 메서드입니다.
   *
   * @return 현재 계정 정보 또는 null
   */
  public Account getCurrentAccount() {
    return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account ? (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
  }
}
