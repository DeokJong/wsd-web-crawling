package com.wsd.web.wsd_web_crawling.bookmarks.dto;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 북마크 응답 DTO 클래스.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class BookmarksResponse extends BaseDto {
  
    /**
     * 잡 포스팅 상세 응답 정보.
     */
    private JobPostingDetailResponse jobPostingDetailResponse;

}