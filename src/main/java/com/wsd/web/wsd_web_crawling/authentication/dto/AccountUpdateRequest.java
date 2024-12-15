package com.wsd.web.wsd_web_crawling.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계정 업데이트 요청을 나타내는 클래스입니다.
 * 이 클래스는 비밀번호와 닉네임을 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateRequest {
  /**
   * 바꿀 비밀번호
   */
  @NotBlank
  @Schema(description = "바꿀 비밀번호", example = "Password12345!")
  private String password;

  /**
   * 변경할 닉네임
   */
  @NotBlank
  @Schema(description = "변경할 닉네임", example = "test123")
  private String nickname;
}
