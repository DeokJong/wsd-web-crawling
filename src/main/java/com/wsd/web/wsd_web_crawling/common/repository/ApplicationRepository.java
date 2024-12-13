package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByAccountAndJobPosting(Account account, JobPosting jobPosting);
    List<Application> findByAccount(Account account);
}