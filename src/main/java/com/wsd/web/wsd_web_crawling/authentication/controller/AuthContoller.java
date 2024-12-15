package com.wsd.web.wsd_web_crawling.authentication.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wsd.web.wsd_web_crawling.authentication.dto.AccountCreateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.AccountUpdateRequest;
import com.wsd.web.wsd_web_crawling.authentication.dto.LoginRequest;
import com.wsd.web.wsd_web_crawling.authentication.service.AuthService;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthContoller {
  private final AuthService authService;

  /**
   * 로그인 메서드
   * 
   * @param request 로그인 요청 정보
   * @param response HTTP 응답
   * @return 로그인 성공 응답
   */
  @PostMapping("/login")
  public ResponseEntity<Response<?>> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {

    authService.login(request, response);

    Response<?> body = Response.createResponseWithoutData(HttpStatus.OK.value(), "로그인이 성공적으로 완료되었습니다.");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  /**
   * 로그아웃 메서드
   * 
   * @param response HTTP 응답
   * @return 로그아웃 성공 응답
   */
  @PostMapping("/logout")
  public ResponseEntity<Response<?>> logout(HttpServletRequest request, HttpServletResponse httpResponse) {

    Response<?> response = authService.logout(request, httpResponse);

    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  /**
   * 회원가입 메서드
   * 
   * @param request 회원가입 요청 정보
   * @param response HTTP 응답
   * @return 회원가입 성공 응답
   */
  @PostMapping("/register")
  public ResponseEntity<Response<?>> signup(@RequestBody @Valid AccountCreateRequest request,
      HttpServletResponse httpResponse) {

    Response<?> response = authService.register(request);

    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  /**
   * 토큰 갱신 메서드
   * 
   * @param request HTTP 요청
   * @param response HTTP 응답
   * @return 토큰 갱신 성공 응답
   */
  @PostMapping("/refresh")
  public ResponseEntity<Response<?>> refreshToken(HttpServletRequest request, HttpServletResponse httpResponse) {

    Response<?> response = authService.getAccessTokenRefresh(request, httpResponse);

    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
  }

  /**
   * 프로필 업데이트 메서드
   * 
   * @param updateRequest 업데이트 요청 정보
   * @param request HTTP 요청
   * @param response HTTP 응답
   * @return 프로필 업데이트 성공 응답
   */
  @PutMapping("/profile")
  public ResponseEntity<Response<?>> updateProfile(@RequestBody @Valid AccountUpdateRequest updateRequest,
      HttpServletRequest request, HttpServletResponse response) {

    authService.updateProfile(updateRequest, request, response);

    Response<?> body = Response.createResponseWithoutData(200, "회원정보 수정 성공");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

}
