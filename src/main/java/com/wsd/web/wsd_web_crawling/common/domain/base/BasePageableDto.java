package com.wsd.web.wsd_web_crawling.common.domain.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BasePageableDto extends BaseDto {
  @Default
  @Schema(description = "페이지 번호", example = "1")
  private int page = 1;
  @Default
  @Schema(description = "페이지 크기", example = "20")
  private int size = 20;

  public Pageable toPageable() {
    return PageRequest.of(this.page - 1, this.size);
  }
}