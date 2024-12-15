package com.wsd.web.wsd_web_crawling.authentication.components;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

  private final RedisTemplate<String, String> redisTemplate;
  @Value("${jwt.authorization-header-refresh}")
  private String AUTHORIZATION_HEADER_REFRESH;
  @Value("${jwt.refresh-token-validity-in-seconds}")
  private long REFRESH_TOKEN_EXPIRATION;

    public void saveRefreshToken(String username, String refreshToken) {
      String key = getKey(username, refreshToken);
      redisTemplate.opsForValue().set(key, "", Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION));
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
      String key = getKey(username, refreshToken);
      return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteRefreshToken(String username, String refreshToken) {
      String key = getKey(username, refreshToken);
      redisTemplate.delete(key);
    }

    private String getKey(String username, String refreshToken) {
    return AUTHORIZATION_HEADER_REFRESH + username + ":" + refreshToken;
  }
}
