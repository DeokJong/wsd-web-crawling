package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet;

import com.wsd.web.wsd_web_crawling.common.domain.base.BasePageableDto;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.model.DateOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * ApplicationGetRequest는 지원 상태 및 날짜 정렬 기준을 포함한 페이지네이션 요청 DTO입니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ApplicationGetRequest extends BasePageableDto {

  /**
   * 지원 상태를 나타냅니다. 가능한 값은 APPLIED, REJECTED, INTERVIEW, UNDER_REVIEW, HIRED, CANCELLED, UNKNOWN, ALL입니다.
   * 기본값은 ALL입니다.
   */
  @Schema(description = "지원 상태 (APPLIED, REJECTED, INTERVIEW, UNDER_REVIEW, HIRED, CANCELLED, UNKNOWN, All)", example = "APPLIED")
  @Builder.Default
  private ApplicationStatus status = ApplicationStatus.ALL;

  /**
   * 날짜 정렬 방식을 나타냅니다. 가능한 값은 ASC(오름차순) 및 DESC(내림차순)입니다.
   * 기본값은 ASC입니다.
   */
  @Schema(description = "날짜 정렬(ASC, DESC)", example = "ASC")
  @Builder.Default
  private DateOrder dateOrder = DateOrder.ASC;

  /**
   * 페이지네이션 정보를 포함하는 Pageable 객체를 생성합니다.
   *
   * @return 정렬 기준과 함께 페이지 정보를 포함하는 Pageable 객체
   */
  @Override
  public Pageable toPageable() {
    Sort sort = Sort.by("appliedAt");
    if (dateOrder == DateOrder.DESC) {
      sort = sort.descending();
    } else {
      sort = sort.ascending();
    }
    return PageRequest.of(this.getPage() - 1, this.getSize(), sort);
  }
}
