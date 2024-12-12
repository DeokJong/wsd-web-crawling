package com.wsd.web.wsd_web_crawling.authentication.components;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JsonWebTokenProvider {
  private static final String AUTHORITIES_KEY = "auth";

  private final String secret;
  public final long tokenValidityInMilliseconds;
  public final long refreshTokenValidityInMilliseconds;
  public final String AUTHORIZATION_HEADER_ACCESS;
  public final String AUTHORIZATION_HEADER_REFRESH;
  private final UserDetailsService userDetailsService;

  private Key key;
  private JwtParser jwtParser;

  public JsonWebTokenProvider(
      @Value("${jwt.access-secret}") String secret,
      @Value("${jwt.access-token-validity-in-seconds}") long tokenValidityInSeconds,
      @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
      @Value("${jwt.authorization-header-access}") String AUTHORIZATION_HEADER_ACCESS,
      @Value("${jwt.authorization-header-refresh}") String AUTHORIZATION_HEADER_REFRESH,
      UserDetailsService userDetailsService) {

    if (tokenValidityInSeconds <= 0) {
      throw new IllegalArgumentException("토큰 유효 기간은 0보다 커야 합니다.");
    }

    if (refreshTokenValidityInSeconds <= 0) {
      throw new IllegalArgumentException("Refresh 토큰 유효 기간은 0보다 커야 합니다.");
    }

    this.secret = secret;
    this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    this.AUTHORIZATION_HEADER_ACCESS = AUTHORIZATION_HEADER_ACCESS;
    this.AUTHORIZATION_HEADER_REFRESH = AUTHORIZATION_HEADER_REFRESH;
    this.userDetailsService = userDetailsService;
  }

  @PostConstruct
  public void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
  }

  /**
   * Authentication 객체의 권한 정보를 이용하여 JWT 토큰을 생성하는 메소드
   * 
   * @param authentication 인증 정보
   * @return 생성된 JWT 토큰
   */
  public String createToken(Authentication authentication) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    Date now = new Date();
    Date validity = new Date(now.getTime() + this.tokenValidityInMilliseconds);

    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim(AUTHORITIES_KEY, authorities)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Refresh Token 생성 메소드
   * 
   * @param authentication 인증 정보
   * @return 생성된 Refresh Token
   */
  public String createRefreshToken(Authentication authentication) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + this.refreshTokenValidityInMilliseconds);

    return Jwts.builder()
        .setSubject(authentication.getName())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * JWT 토큰으로부터 인증 정보를 조회하는 메소드
   * 
   * @param token JWT 토큰
   * @return 인증 정보
   */
  public Authentication getAuthentication(String token) {
    if (!validateToken(token)) {
      return null;
    }

    Claims claims = jwtParser.parseClaimsJws(token).getBody();
    String username = claims.getSubject();

    // UserDetailsService를 사용하여 사용자 정보 로드
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Authentication 객체 생성 및 반환
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  /**
   * 토큰의 유효성을 검증하는 메소드
   * 
   * @param authToken 검증할 JWT 토큰
   * @return 유효성 검사 결과
   */
  public boolean validateToken(String authToken) {

    try {
      jwtParser.parseClaimsJws(authToken);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.info("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Refresh Token의 유효성을 검증하는 메소드
   * 
   * @param refreshToken 검증할 Refresh Token
   * @return 유효성 검사 결과
   */
  public boolean validateRefreshToken(String refreshToken) {
    try {
      jwtParser.parseClaimsJws(refreshToken);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.info("유효하지 않은 Refresh Token입니다: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Refresh Token에서 새로운 Access Token 생성
   * 
   * @param refreshToken Refresh Token
   * @return 생성된 Access Token
   */
  public String createTokenFromRefreshToken(String refreshToken) {
    Claims claims = jwtParser.parseClaimsJws(refreshToken).getBody();
    String username = claims.getSubject();

    // 사용자 정보 로드
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "",
        userDetails.getAuthorities());

    return createToken(authentication);
  }

  /**
   * 토큰의 남은 유효 시간 (밀리초 단위) 반환
   * 
   * @param token JWT 토큰
   * @return 남은 유효 시간
   */
  public long getExpiration(String token) {
    Claims claims = jwtParser.parseClaimsJws(token).getBody();
    Date expiration = claims.getExpiration();
    return expiration.getTime() - new Date().getTime();
  }

  /**
   * Refresh Token의 남은 유효 시간 (밀리초 단위) 반환
   * 
   * @param refreshToken Refresh Token
   * @return 남은 유효 시간
   */
  public long getRefreshTokenExpiration(String refreshToken) {
    Claims claims = jwtParser.parseClaimsJws(refreshToken).getBody();
    Date expiration = claims.getExpiration();
    return expiration.getTime() - new Date().getTime();
  }

  /**
   * 토큰에서 사용자 이름 추출
   * 
   * @param request HTTP 요청
   * @return 사용자 이름
   */
  public Optional<String> getUsernameFromRequest(HttpServletRequest request) {
    return Optional.ofNullable(request.getCookies())
        .flatMap(cookies -> Arrays.stream(cookies)
            .filter(cookie -> AUTHORIZATION_HEADER_REFRESH.equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue))
        .filter(this::validateToken)
        .map(token -> jwtParser.parseClaimsJws(token).getBody().getSubject());
  }

  /**
   * 쿠키 생성
   * 
   * @param name 쿠키 이름
   * @param value 쿠키 값
   * @param maxAge 쿠키 최대 수명 (밀리초 단위)
   * @return 생성된 쿠키
   */
  public Cookie createCookie(String name, String value, long maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (maxAge / 1000));
    return cookie;
  }

  /**
   * 응답에서 쿠키 삭제
   * 
   * @param response HTTP 응답
   * @param name 삭제할 쿠키 이름
   */
  public void deleteCookie(HttpServletResponse response, String name) {
    Cookie deleteCookie = new Cookie(name, null);
    deleteCookie.setPath("/");
    deleteCookie.setHttpOnly(true);
    deleteCookie.setMaxAge(0);
    response.addCookie(deleteCookie);
  }
}
