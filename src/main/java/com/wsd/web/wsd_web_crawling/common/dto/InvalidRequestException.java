package com.wsd.web.wsd_web_crawling.common.dto;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
/**
 * InvalidRequestException은 잘못된 요청에 대한 예외를 나타냅니다.
 * 이 예외는 BindingResult를 포함하여 요청의 유효성 검사 결과를 제공합니다.
 */
public class InvalidRequestException extends RuntimeException {

    private final BindingResult bindingResult;

    /**
     * 주어진 BindingResult로 InvalidRequestException을 생성합니다.
     *
     * @param bindingResult 요청의 유효성 검사 결과
     */
    public InvalidRequestException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    /**
     * 주어진 메시지와 BindingResult로 InvalidRequestException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param bindingResult 요청의 유효성 검사 결과
     */
    public InvalidRequestException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    /**
     * 주어진 메시지, 원인 및 BindingResult로 InvalidRequestException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     * @param bindingResult 요청의 유효성 검사 결과
     */
    public InvalidRequestException(String message, Throwable cause, BindingResult bindingResult) {
        super(message, cause);
        this.bindingResult = bindingResult;
    }

    /**
     * 주어진 원인과 BindingResult로 InvalidRequestException을 생성합니다.
     *
     * @param cause 원인 예외
     * @param bindingResult 요청의 유효성 검사 결과
     */
    public InvalidRequestException(Throwable cause, BindingResult bindingResult) {
        super(cause);
        this.bindingResult = bindingResult;
    }
}
