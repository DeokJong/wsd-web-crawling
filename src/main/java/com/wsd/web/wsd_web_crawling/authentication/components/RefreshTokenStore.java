package com.wsd.web.wsd_web_crawling.authentication.components;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * RefreshTokenStore는 리프레시 토큰의 저장, 검증 및 삭제를 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.authorization-header-refresh}")
    private String AUTHORIZATION_HEADER_REFRESH;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long REFRESH_TOKEN_EXPIRATION;

    /**
     * 사용자의 리프레시 토큰을 저장합니다.
     *
     * @param username     사용자 이름
     * @param refreshToken 저장할 리프레시 토큰
     */
    public void saveRefreshToken(String username, String refreshToken) {
        String key = getKey(username, refreshToken);
        redisTemplate.opsForValue().set(key, "", Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION));
    }

    /**
     * 사용자의 리프레시 토큰의 유효성을 검증합니다.
     *
     * @param username     사용자 이름
     * @param refreshToken 검증할 리프레시 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateRefreshToken(String username, String refreshToken) {
        String key = getKey(username, refreshToken);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 사용자의 리프레시 토큰을 삭제합니다.
     *
     * @param username     사용자 이름
     * @param refreshToken 삭제할 리프레시 토큰
     */
    public void deleteRefreshToken(String username, String refreshToken) {
        String key = getKey(username, refreshToken);
        redisTemplate.delete(key);
    }

    /**
     * 사용자 이름과 리프레시 토큰을 기반으로 Redis 키를 생성합니다.
     *
     * @param username     사용자 이름
     * @param refreshToken 리프레시 토큰
     * @return 생성된 Redis 키
     */
    private String getKey(String username, String refreshToken) {
        return AUTHORIZATION_HEADER_REFRESH + username + ":" + refreshToken;
    }
}
