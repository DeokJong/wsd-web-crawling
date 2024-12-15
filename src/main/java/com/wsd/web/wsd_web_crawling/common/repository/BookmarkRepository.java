package com.wsd.web.wsd_web_crawling.common.repository;
  
import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;

/**
 * 즐겨찾기 엔티티에 대한 데이터 접근을 담당하는 리포지토리 인터페이스입니다.
 */
@Repository
@DynamicUpdate
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 주어진 계정 ID로 즐겨찾기를 조회합니다.
     *
     * @param accountId 사용자 계정 ID
     * @return 해당 계정의 즐겨찾기가 존재하면 Optional에 담겨 반환됩니다.
     */
    Optional<Bookmark> findByAccountId(Long accountId);

    /**
     * 주어진 계정 ID로 즐겨찾기를 페이지 단위로 조회합니다.
     *
     * @param accountId 사용자 계정 ID
     * @param pageable 페이지 정보
     * @return 해당 계정의 즐겨찾기 목록을 페이지 단위로 반환합니다.
     */
    Page<Bookmark> findByAccountId(Long accountId, Pageable pageable);
}
  