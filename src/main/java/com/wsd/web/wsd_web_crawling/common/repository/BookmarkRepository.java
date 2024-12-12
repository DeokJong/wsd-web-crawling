package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wsd.web.wsd_web_crawling.common.domain.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  Optional<Bookmark> findByAccountId(Long accountId);
}
