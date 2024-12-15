package com.wsd.web.wsd_web_crawling.authentication.config;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wsd.web.wsd_web_crawling.authentication.components.AccountDetailsService;
import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenAccessDeniedHandler;
import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenAuthenticationEntryPoint;
import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenAuthenticationFilter;

/**
 * Spring Security 설정을 위한 구성 클래스입니다.
 * 이 클래스는 웹 보안 기능을 활성화하고, JWT 기반 인증 및 권한 부여를 설정합니다.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
  private final JsonWebTokenAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JsonWebTokenAccessDeniedHandler jwtAccessDeniedHandler;
  private final AccountDetailsService accountDetailsService;
  private final JsonWebTokenAuthenticationFilter accountAuthenticationFilter;

  /**
   * HTTP 보안 설정을 위한 필터 체인을 구성합니다.
   *
   * @param http HttpSecurity 객체
   * @return SecurityFilterChain 객체
   * @throws Exception 보안 설정 중 발생할 수 있는 예외
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/register", "/api/auth/logout").permitAll()
            .requestMatchers("/docs","/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**")
            .permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/user/**").hasRole("USER")
            .anyRequest().authenticated())
        .addFilterBefore(accountAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * 인증 관리자 빈을 생성합니다.
   *
   * @param http HttpSecurity 객체
   * @return AuthenticationManager 객체
   * @throws Exception 인증 관리자 생성 중 발생할 수 있는 예외
   */
  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
    auth.userDetailsService(accountDetailsService).passwordEncoder(passwordEncoder());
    return auth.build();
  }

  /**
   * 비밀번호 인코더를 생성합니다.
   *
   * @return PasswordEncoder 객체
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
