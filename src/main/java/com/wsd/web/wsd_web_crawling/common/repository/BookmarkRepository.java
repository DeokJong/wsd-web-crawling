package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;

@Repository
@DynamicUpdate
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  Optional<Bookmark> findByAccountId(Long accountId);
}
