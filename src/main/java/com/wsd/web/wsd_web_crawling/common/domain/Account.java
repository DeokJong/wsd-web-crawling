package com.wsd.web.wsd_web_crawling.common.domain;

import com.wsd.web.wsd_web_crawling.common.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * 계정 엔티티 클래스
 * 이 클래스는 사용자 계정 정보를 나타냅니다.
 */
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {
  /**
   * 계정 ID
   * 자동 생성되는 고유 식별자입니다.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 사용자 닉네임
   * 사용자에게 표시되는 이름입니다.
   */
  @Column(nullable = false)
  private String nickname;

  /**
   * 사용자 이름
   * 고유해야 하며, 로그인 시 사용됩니다.
   */
  @Column(nullable = false, unique = true)
  private String username;

  /**
   * 비밀번호
   * 사용자 계정의 보안을 위해 필요합니다.
   */
  @Column(nullable = false)
  private String password;

  /**
   * 사용자 역할
   * 계정의 권한을 정의합니다. 기본값은 ROLE_USER입니다.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  @Builder.Default
  private Role role = Role.ROLE_USER;
}
