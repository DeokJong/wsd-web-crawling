package com.wsd.web.wsd_web_crawling.authentication.components;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 세부 정보를 로드하는 서비스 클래스입니다.
 * 이 클래스는 Spring Security의 UserDetailsService를 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {
  private final AccountRepository accountRepository;

  /**
   * 주어진 사용자 이름으로 사용자 세부 정보를 로드합니다.
   *
   * @param username 사용자 이름
   * @return UserDetails 사용자 세부 정보
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    return new AccountDetails(account);
  }
  
}
