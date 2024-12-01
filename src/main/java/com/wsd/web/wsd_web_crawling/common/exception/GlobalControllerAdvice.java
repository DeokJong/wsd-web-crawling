package com.wsd.web.wsd_web_crawling.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wsd.web.wsd_web_crawling.common.dto.FieldErrorResponse;
import com.wsd.web.wsd_web_crawling.common.dto.InvalidRequestException;
import com.wsd.web.wsd_web_crawling.common.dto.Response;

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
}
