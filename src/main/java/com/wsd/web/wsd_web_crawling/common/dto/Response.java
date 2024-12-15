package com.wsd.web.wsd_web_crawling.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기본적인 응답 객체입니다.
 * 이후 추가될 응답 객체는 이 클래스를 상속받아 사용합니다.
 *
 * @param <T> 데이터의 타입
 */
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Response<T> {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;
  private int status;
  private String message;
  private T data;

    /**
     * 응답 객체를 생성하는 생성자입니다.
     *
     * @param status 응답 상태 코드
     * @param message 응답 메시지
     * @param data 응답 데이터
     */
    private Response(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * 응답 객체를 생성하는 정적 메서드입니다.
     *
     * @param status 응답 상태 코드
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @return 생성된 응답 객체
     */
    public static <T> Response<T> createResponse(int status, String message, T data) {
        return new Response<>(status, message, data);
    }

    /**
     * 데이터 없이 응답 객체를 생성하는 정적 메서드입니다.
     *
     * @param status 응답 상태 코드
     * @param message 응답 메시지
     * @return 생성된 응답 객체
     */
    public static Response<?> createResponseWithoutData(int status, String message) {
        return new Response<>(status, message, null);
    }
}

