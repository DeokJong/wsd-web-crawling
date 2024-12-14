package com.wsd.web.wsd_web_crawling.common.repository;

import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wsd.web.wsd_web_crawling.common.domain.Account;

/**
 * AccountRepository는 Account 엔티티에 대한 CRUD 작업을 수행하는 인터페이스입니다.
 * 이 인터페이스는 Spring Data JPA의 JpaRepository를 확장합니다.
 */
@Repository
@DynamicUpdate
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * 주어진 사용자 이름으로 Account를 찾습니다.
     *
     * @param username 사용자 이름
     * @return 사용자 이름에 해당하는 Account가 존재하면 Optional<Account>를 반환합니다.
     */
    public Optional<Account> findByUsername(String username);

    /**
     * 주어진 사용자 이름으로 Account의 존재 여부를 확인합니다.
     *
     * @param username 사용자 이름
     * @return 사용자 이름에 해당하는 Account가 존재하면 true, 그렇지 않으면 false를 반환합니다.
     */
    public boolean existsByUsername(String username);
}
