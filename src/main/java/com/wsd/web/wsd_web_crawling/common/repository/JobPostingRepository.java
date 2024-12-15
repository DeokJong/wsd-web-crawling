package com.wsd.web.wsd_web_crawling.common.repository;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;

/**
 * JobPostingRepository는 JobPosting 엔티티에 대한 CRUD 작업을 수행하는 인터페이스입니다.
 * 이 인터페이스는 JpaRepository를 확장하여 기본적인 데이터베이스 작업을 제공합니다.
 */
@Repository
@DynamicUpdate
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

  /**
   * 주어진 고유 식별자가 존재하는지 확인합니다.
   *
   * @param uniqueIdentifier 확인할 고유 식별자
   * @return 존재하면 true, 그렇지 않으면 false
   */
  boolean existsByUniqueIdentifier(String uniqueIdentifier);

  /**
   * 고유 식별자로 JobPosting 찾기
   *
   * @param uniqueIdentifier 고유 식별자
   * @return JobPosting 엔티티
   */
  JobPosting findByUniqueIdentifier(String uniqueIdentifier);

  /**
   * 고유 식별자로 JobPosting을 페이징하여 찾기
   *
   * @param uniqueIdentifier 고유 식별자
   * @param pageable         페이징 정보
   * @return 페이징된 JobPosting
   */
  Page<JobPosting> findByUniqueIdentifier(String uniqueIdentifier, Pageable pageable);

  /**
   * Sector 필드에서 주어진 키워드를 포함하는 JobPosting을 페이징하여 찾기
   *
   * @param keyword  검색할 키워드
   * @param location 검색할 지역
   * @param pageable 페이징 정보
   * @return 페이징된 JobPosting 리스트
   */
  @Query("SELECT jp FROM JobPosting jp " +
  "WHERE (:keyword IS NULL OR :keyword = '' OR jp.sector LIKE CONCAT('%', :keyword, '%')) " +
  "AND (:location IS NULL OR :location = '' OR jp.location LIKE CONCAT('%', :location, '%'))")
  Page<JobPosting> findByKeywordAndLocation(
    @Param("keyword") String keyword,
    @Param("location") String location,
    Pageable pageable);
}

