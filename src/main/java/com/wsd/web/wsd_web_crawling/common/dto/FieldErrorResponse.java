package com.wsd.web.wsd_web_crawling.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 필드 오류 응답을 나타내는 클래스입니다.
 */
@Getter @Setter
@AllArgsConstructor
public class FieldErrorResponse {
    /**
     * 오류가 발생한 필드의 이름입니다.
     */
    private String field;

    /**
     * 오류 코드입니다.
     */
    private String code;

    /**
     * 오류 메시지입니다.
     */
    private String message;
}
