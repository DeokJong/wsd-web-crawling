package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.domain.Account;

@Repository
@DynamicUpdate
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  Optional<Application> findByAccountAndJobPostingId(Account account, Long jobPostingId);
  List<Application> findByAccount(Account account);
}