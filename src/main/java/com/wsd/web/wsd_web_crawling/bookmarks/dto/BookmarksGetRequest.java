package com.wsd.web.wsd_web_crawling.bookmarks.dto;

import com.wsd.web.wsd_web_crawling.common.domain.base.BasePageableDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BookmarksGetRequest 클래스는 북마크 데이터를 조회하기 위한 요청 정보를 담고 있습니다.
 * 이 클래스는 BasePageableDto를 상속받아 페이징 관련 기능을 제공합니다.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BookmarksGetRequest extends BasePageableDto {
  
}