package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.domain.Account;

/**
 * Application 엔티티에 대한 데이터 액세스 계층.
 */
@Repository
@DynamicUpdate
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * 특정 계정과 채용 공고 ID로 신청서를 조회합니다.
     *
     * @param account 사용자의 계정 정보
     * @param jobPostingId 채용 공고의 ID
     * @return 조건에 맞는 신청서의 Optional 객체
     */
    Optional<Application> findByAccountAndJobPostingId(Account account, Long jobPostingId);

    /**
     * 특정 계정의 모든 신청서를 페이징 처리하여 조회합니다.
     *
     * @param account 사용자의 계정 정보
     * @param pageable 페이징 정보
     * @return 신청서 페이지
     */
    Page<Application> findByAccount(Account account, Pageable pageable);

    /**
     * 특정 계정과 상태에 해당하는 신청서를 페이징 처리하여 조회합니다.
     *
     * @param account 사용자의 계정 정보
     * @param status 신청서의 상태
     * @param pageable 페이징 정보
     * @return 신청서 페이지
     */
    Page<Application> findByAccountAndStatus(Account account, ApplicationStatus status, Pageable pageable);
}
