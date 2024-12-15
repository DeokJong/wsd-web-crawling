package com.wsd.web.wsd_web_crawling.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.wsd.web.wsd_web_crawling.common.dto.FieldErrorResponse;
import com.wsd.web.wsd_web_crawling.common.dto.InvalidRequestException;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

  /**
   * InvalidRequestException을 처리하는 메서드.
   *
   * @param e 처리할 InvalidRequestException
   * @return ResponseEntity에 포함된 FieldErrorResponse 리스트와 함께 BAD_REQUEST 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<List<FieldErrorResponse>>> handleInvalidRequestException(InvalidRequestException e) {
    log.error("InvalidRequestException occurred in {}  {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString(),
              e.getStackTrace()[3].toString(),
              e.getStackTrace()[4].toString());

    List<FieldErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> new FieldErrorResponse(fieldError.getField(), fieldError.getCode(),
            fieldError.getDefaultMessage())

        ).toList();

    return new ResponseEntity<>(Response.createResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), errors),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * MethodArgumentNotValidException을 처리하는 메서드.
   *
   * @param e 처리할 MethodArgumentNotValidException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 UNPROCESSABLE_ENTITY 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("MethodArgumentNotValidException occurred in {}  {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString(),
              e.getStackTrace()[3].toString(),
              e.getStackTrace()[4].toString());

    List<FieldErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> new FieldErrorResponse(fieldError.getField(), fieldError.getCode(),
            fieldError.getDefaultMessage())
        ).toList();

    String defaultMessage = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getDefaultMessage())
        .findFirst()
        .orElse(e.getMessage());

    return new ResponseEntity<>(Response.createResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), defaultMessage, errors),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * 일반 예외를 처리하는 메서드.
   *
   * @param e 처리할 일반 예외
   * @return ResponseEntity에 포함된 오류 메시지와 함께 적절한 HTTP 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleGenericException(Exception e) {
    if (e instanceof UsernameNotFoundException) {
        return handleUsernameNotFoundException((UsernameNotFoundException) e);
    } else if (e instanceof AccessDeniedException) {
        return handleAccessDeniedException((AccessDeniedException) e);
    } else if (e instanceof InvalidRequestException) {
        ResponseEntity<Response<List<FieldErrorResponse>>> response = handleInvalidRequestException((InvalidRequestException) e);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    } else if (e instanceof ResponseStatusException) {
        return new ResponseEntity<>(Response.createResponseWithoutData(((ResponseStatusException) e).getStatusCode().value(), e.getMessage()), ((ResponseStatusException) e).getStatusCode());
    }
    
    log.error("Generic Exception occurred in {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(),
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString(),
              e.getStackTrace()[3].toString(),
              e.getStackTrace()[4].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * AccessDeniedException을 처리하는 메서드.
   *
   * @param e 처리할 AccessDeniedException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 FORBIDDEN 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleAccessDeniedException(AccessDeniedException e) {
    log.error("AccessDeniedException occurred in {}  {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString(),
              e.getStackTrace()[3].toString(),
              e.getStackTrace()[4].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.FORBIDDEN.value(), e.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  /**
   * UsernameNotFoundException을 처리하는 메서드.
   *
   * @param e 처리할 UsernameNotFoundException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 NOT_FOUND 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleUsernameNotFoundException(UsernameNotFoundException e) {
    log.error("UsernameNotFoundException occurred in {}  {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString(),
              e.getStackTrace()[3].toString(),
              e.getStackTrace()[4].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), e.getMessage()),
        HttpStatus.NOT_FOUND);
  }

  /**
   * AuthenticationException을 처리하는 메서드.
   *
   * @param e 처리할 BadCredentialsException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 UNAUTHORIZED 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleAuthenticationException(BadCredentialsException e) {
    log.error("AuthenticationException occurred in {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  /**
   * HttpMessageNotReadableException을 처리하는 메서드.
   *
   * @param e 처리할 HttpMessageNotReadableException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 UNPROCESSABLE_ENTITY 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.error("HttpMessageNotReadableException occurred in {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.UNPROCESSABLE_ENTITY.value(), "올바른 형식이 아닙니다."), HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * IllegalArgumentException을 처리하는 메서드.
   *
   * @param e 처리할 IllegalArgumentException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 BAD_REQUEST 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("IllegalArgumentException occurred in {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  /**
   * JwtException을 처리하는 메서드.
   *
   * @param e 처리할 JwtException
   * @return ResponseEntity에 포함된 오류 메시지와 함께 UNAUTHORIZED 상태 반환
   */
  @ExceptionHandler
  public ResponseEntity<Response<?>> handleJwtException(JwtException e) {
    log.error("JwtException occurred in {}  {}  {}  {}  {}  {}", 
              e.getClass().getSimpleName(), 
              e.getMessage(), 
              e.getStackTrace()[0].toString(),
              e.getStackTrace()[1].toString(),
              e.getStackTrace()[2].toString());
    return new ResponseEntity<>(Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
  }
}