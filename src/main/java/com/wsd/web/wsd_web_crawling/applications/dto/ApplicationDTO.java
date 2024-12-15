package com.wsd.web.wsd_web_crawling.applications.dto;

import java.time.LocalDateTime;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Application Data Transfer Object (DTO) 클래스.
 * 지원서 관련 데이터를 전달하는 데 사용됩니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ApplicationDTO extends BaseDto {

    /**
     * 지원서의 고유 식별자.
     */
    private Long applicationId;

    /**
     * 지원서의 상태.
     */
    private String status;

    /**
     * 해당 직무 공고의 고유 식별자.
     */
    private Long jobPostingId;

    /**
     * 직무 공고의 제목.
     */
    private String jobPostingTitle;

    /**
     * 직무 공고를 올린 회사명.
     */
    private String jobPostingCompany;

    /**
     * 직무 공고의 링크.
     */
    private String jobPostingLink;

    /**
     * 지원한 날짜 및 시간.
     */
    private LocalDateTime appliedAt;
}
