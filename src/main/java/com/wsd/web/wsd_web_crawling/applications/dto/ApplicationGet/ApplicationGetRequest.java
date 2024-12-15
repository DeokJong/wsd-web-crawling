package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet;

import com.wsd.web.wsd_web_crawling.common.domain.base.BasePageableDto;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.model.DateOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ApplicationGetRequest extends BasePageableDto {

  @Schema(description = "지원 상태 (APPLIED, REJECTED, INTERVIEW, UNDER_REVIEW, HIRED, CANCELLED, UNKNOWN, All)", example = "APPLIED")
  private ApplicationStatus status;

  @Schema(description = "날짜 정렬(ASC, DESC)", example = "ASC")
  private DateOrder dateOrder;

  @Override
  public Pageable toPageable() {
    Sort sort = Sort.by("appliedAt");
    if (dateOrder == DateOrder.DESC) {
      sort = sort.descending();
    } else {
      sort = sort.ascending();
    }
    return PageRequest.of(this.getPage()-1, this.getSize(), sort);
  }
}
