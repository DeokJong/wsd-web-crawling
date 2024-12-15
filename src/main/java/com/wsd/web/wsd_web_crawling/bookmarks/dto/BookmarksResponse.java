package com.wsd.web.wsd_web_crawling.bookmarks.dto;

import com.wsd.web.wsd_web_crawling.common.domain.base.BaseDto;
import com.wsd.web.wsd_web_crawling.jobs.dto.JobPostingDetail.JobPostingDetailResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class BookmarksResponse extends BaseDto {
  private JobPostingDetailResponse jobPostingDetailResponse;

}