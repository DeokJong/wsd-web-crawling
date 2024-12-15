package com.wsd.web.wsd_web_crawling.common.domain.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * BasePageableDto 클래스는 페이지 정보와 기본 DTO 기능을 제공합니다.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BasePageableDto extends BaseDto {
  
  /**
   * 페이지 번호를 나타냅니다. 기본값은 1입니다.
   */
  @Default
  @Schema(description = "페이지 번호", example = "1")
  @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
  private int page = 1;
  
  /**
   * 페이지 크기를 나타냅니다. 기본값은 20입니다.
   */
  @Default
  @Schema(description = "페이지 크기", example = "20")
  @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
  private int size = 20;

  /**
   * Pageable 객체를 생성합니다.
   * 
   * @return 페이지 요청을 나타내는 Pageable 객체
   */
  public Pageable toPageable() {
    return PageRequest.of(this.page - 1, this.size);
  }
}
