package com.wsd.web.wsd_web_crawling.authentication.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * CORS 설정을 담당하는 구성 클래스입니다.
 * 이 클래스는 애플리케이션의 CORS 정책을 설정하고 관리합니다.
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    /**
     * 허용된 HTTP 메서드 목록을 지정합니다.
     */
    @Value("${allowed.methods}")
    private final String[] ALLOWED_METHODS;

    /**
     * 허용된 오리진(도메인) 목록을 지정합니다.
     */
    @Value("${allowed.origins}")
    private final String[] ALLOWED_ORIGINS;

    /**
     * CORS 구성 소스를 생성하여 반환합니다.
     *
     * @return 구성된 {@link CorsConfigurationSource} 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGINS));  // 허용된 도메인 설정
        configuration.setAllowedMethods(Arrays.asList(ALLOWED_METHODS));  // 허용된 메서드 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));  // 모든 헤더 허용
        configuration.setAllowCredentials(true);  // 쿠키, 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
    
}
