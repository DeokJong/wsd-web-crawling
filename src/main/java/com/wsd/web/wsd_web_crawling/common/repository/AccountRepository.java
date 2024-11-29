package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wsd.web.wsd_web_crawling.common.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
  public Optional<Account> findByUsername(String username);

  public boolean existsByUsername(String username);
}
