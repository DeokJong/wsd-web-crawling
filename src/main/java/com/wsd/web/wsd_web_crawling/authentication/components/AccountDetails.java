package com.wsd.web.wsd_web_crawling.authentication.components;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wsd.web.wsd_web_crawling.common.domain.Account;

import lombok.AllArgsConstructor;

/**
 * AccountDetails 클래스는 UserDetails 인터페이스를 구현하여
 * 사용자 계정 정보를 제공하는 클래스입니다.
 */
@AllArgsConstructor
public class AccountDetails implements UserDetails {
  private final Account account;

  @Override
  /**
   * 사용자의 권한을 반환합니다.
   * @return 사용자의 권한 목록
   */
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(account.getRole().name()));
  }

  @Override
  /**
   * 사용자의 비밀번호를 반환합니다.
   * @return 사용자의 비밀번호
   */
  public String getPassword() {
    return account.getPassword();
  }

  @Override
  /**
   * 사용자의 사용자 이름을 반환합니다.
   * @return 사용자의 사용자 이름
   */
  public String getUsername() {
    return account.getUsername();
  }
  
}
