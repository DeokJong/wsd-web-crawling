package com.wsd.web.wsd_web_crawling.auth.components;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wsd.web.wsd_web_crawling.common.domain.Account;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountDetails implements UserDetails {
  private final Account account;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO: 권한 추가
    return null;
  }

  @Override
  public String getPassword() {
    return account.getPassword();
  }

  @Override
  public String getUsername() {
    return account.getUsername();
  }
  
}
