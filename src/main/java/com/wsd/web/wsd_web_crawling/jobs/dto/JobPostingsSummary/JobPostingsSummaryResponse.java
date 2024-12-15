package com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingsSummary;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * JobPostingsSummaryResponse는 채용 공고 요약 정보를 담는 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobPostingsSummaryResponse extends BaseDto {
    
    /**
     * 채용 공고의 고유 식별자입니다.
     */
    private Long id;
    
    /**
     * 채용 공고의 제목입니다.
     */
    private String title;
    
    /**
     * 채용하는 회사의 이름입니다.
     */
    private String company;
    
    /**
     * 채용 공고의 상세 링크입니다.
     */
    private String link;
    
    /**
     * 채용 분야 또는 섹터입니다.
     */
    private String sector;
    
    /**
     * 채용 위치 또는 근무지가 위치한 지역입니다.
     */
    private String location;
}

