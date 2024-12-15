package com.wsd.web.wsd_web_crawling.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계정 생성 요청을 나타내는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateRequest {
  /**
   * 사용자 아이디
   */
  @NotBlank
  @Schema(description = "아이디", example = "test@test.com")
  @Email
  private String username;

  /**
   * 사용자 비밀번호
   */
  @NotBlank
  @Schema(description = "비밀번호", example = "Password1234!")
  private String password;

  /**
   * 사용자 닉네임
   */
  @NotBlank
  @Schema(description = "닉네임", example = "test")
  private String nickname;
}
