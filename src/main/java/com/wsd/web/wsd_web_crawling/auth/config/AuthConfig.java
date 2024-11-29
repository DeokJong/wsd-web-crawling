package com.wsd.web.wsd_web_crawling.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfig {
  @Bean
  public SecurityFilterChain authFilterChain(HttpSecurity http) throws Exception {
    http
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
      .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
      .requestMatchers("/api/auth/**","/api/sign-up", "/api/public/**").permitAll()
      .anyRequest().permitAll()
      );
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    // TODO userDetailsService 구현
    authenticationManagerBuilder.userDetailsService(null).passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
