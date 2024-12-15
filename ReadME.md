
# WSD 웹 크롤링

## 목차

- [프로젝트 개요](#프로젝트-개요)
- [주요 기능](#주요-기능)
- [사용된 기술](#사용된-기술)
- [필수 조건](#필수-조건)
- [설정](#설정)
  - [리포지토리 복제](#리포지토리-복제)
- [실행](#실행)
  - [구성](#구성)
  - [기본 구성](#기본-구성)
    - [Docker 구성](#docker-구성)
    - [로컬 구성](#로컬-구성)
- [프로젝트 구성](#프로젝트-구성)
- [아키텍처 설명](#아키텍처-설명)
  - [주요 아키텍처 계층](#주요-아키텍처-계층)
    - [프레젠테이션 계층](#프레젠테이션-계층)
    - [서비스 계층](#서비스-계층)
    - [데이터 접근 계층](#데이터-접근-계층)
    - [도메인 계층](#도메인-계층)
    - [구성 계층](#구성-계층)

## 프로젝트 개요

WSD 웹 크롤링은 Spring Boot 기반의 웹 애플리케이션으로, 효율적인 웹 크롤링 작업을 수행하도록 설계되었습니다. 보안 인증을 위한 JWT, 캐싱을 위한 Redis, 데이터 저장을 위한 PostgreSQL을 활용하며 Docker를 사용하여 일관된 개발 및 배포 환경을 보장합니다.

## 주요 기능

- **웹 크롤링:** 효율적인 웹 페이지 크롤링 및 처리
- **보안 인증:** Access 및 Refresh 토큰을 지원하는 JWT 기반 인증
- **데이터베이스 통합:** JPA와 Hibernate를 통한 PostgreSQL 데이터 저장
- **캐싱:** 성능 향상을 위한 Redis 캐싱 구현
- **API 문서:** 상호 작용 API 문서를 위한 Swagger UI 통합
- **Docker 지원:** Docker 컨테이너를 사용하여 손쉬운 배포
- **로깅:** 구성 가능한 로깅 수준 지원

## 사용된 기술

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Security**
- **Spring Data JPA**
- **Redis**
- **PostgreSQL**
- **JWT (JSON Web Tokens)**
- **Gradle**
- **Docker**
- **Swagger/OpenAPI**

## 필수 조건

- **Java 17**
- **Gradle 7.6**
- **Docker & Docker Compose**
- **PostgreSQL**
- **Redis**
- **Git**

## 설정

### 리포지토리 복제

```bash
git clone https://github.com/DeokJong/wsd-web-crawling
cd wsd-web-crawling
```

## 실행

run.sh 파일을 실행하여 Docker Container 에다가 애플리케이션을 실행합니다.

```bash
./run.sh
```

만일 초기 데이터를 구성하고 싶다면

```bash
docker exec -i {postgres-container-id} psql -U postgres -d dev < backup.sql
```

## 구성

프로젝트는 로컬 개발과 Docker 배포를 위한 서로 다른 구성을 사용합니다. 구성 파일은 `src/main/resources` 디렉토리에 있습니다.

### 기본 구성

```yml
spring:
  application:
    name: wsd-web-crawling

  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
    properties:
      hibernate:
        "[format_sql]": true
        "[default_batch_fetch_size]": 500
        "[highlight_sql]": true

logging:
  level:
    com:
      wsd:
        web:
          crawling: INFO
    org:
      springframework:
        security: INFO

jwt:
  access-secret: your-secret
  refresh-secret: your-secret
  access-token-validity-in-seconds: 3600
  refresh-token-validity-in-seconds: 604800
  authorization-header-access: AccessToken
  authorization-header-refresh: RefreshToken

allowed:
  origins: http://localhost:3000, http://localhost:5173
  methods: GET, POST, PUT, DELETE, OPTIONS

springdoc:
  swagger-ui:
    path: /docs
```

#### Docker 구성

```yml
spring:
  datasource:
    url: jdbc:postgresql://postgreSQL:5432/dev
    username: postgres
    password: 123456
    driver-class-name: org.postgresql.Driver

  data:
    redis:
      host: redis
      port: 6379
      password: 123456

```

#### 로컬 구성

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dev
    username: postgres
    password: 123456
    driver-class-name: org.postgresql.Driver

  data:
    redis:
      host: localhost
      port: 6379
      password: 123456
```

## 프로젝트 구성

```
wsd-web-crawling/
├── Dockerfile                # Docker 이미지 구성 파일
├── README.md                 # 프로젝트 설명 문서
├── backup.sql                # 데이터베이스 백업 파일
├── build.gradle              # Gradle 빌드 스크립트
├── docker-compose.yml        # Docker Compose 설정 파일
├── gradlew                   # Gradle Wrapper 실행 파일
├── gradlew.bat               # Windows용 Gradle Wrapper 실행 파일
├── instruction.md            # 프로젝트 사용 가이드
├── run.sh                    # 프로젝트 실행 스크립트
├── settings.gradle           # Gradle 설정 파일
├── src/                      # 소스 코드 디렉토리
│   ├── main/                 # 주요 애플리케이션 소스 코드
│   │   ├── java/             # Java 소스 코드
│   │   │   └── com/wsd/web/wsd_web_crawling/
│   │   │       ├── WsdWebCrawlingApplication.java    # 메인 애플리케이션 클래스
│   │   │       ├── applications/                    # 애플리케이션 관련 코드
│   │   │       ├── authentication/                  # 인증 및 보안 관련 코드
│   │   │       ├── bookmarks/                      # 북마크 관리 기능
│   │   │       ├── common/                         # 공통 코드 및 유틸리티
│   │   │       ├── jobs/                           # 크롤링 작업 관련 코드
│   │   └── resources/          # 설정 파일 및 리소스
│       ├── application.yml      # 기본 애플리케이션 설정 파일
│       ├── application-docker.yml  # Docker 환경 설정
│       ├── application-local.yml   # 로컬 개발 환경 설정
│       └── logback-spring.xml      # 로깅 설정 파일
└── build/                     # 빌드 산출물 디렉토리
```

## 아키텍처 설명

- WSD 웹 크롤링 프로젝트는 클라이언트-서버 아키텍처와 레이어드 아키텍처(Layered Architecture) 를 기반으로 설계되었습니다.

### 주요 아키텍처 계층

#### 프레젠테이션 계층

- 설명: 사용자의 요청을 수신하고 응답을 반환합니다.

- 구성 요소: Controller 클래스 (ApplicationsController, AuthController, JobController 등)

#### 서비스 계층

- 설명: 비즈니스 로직을 처리하고 데이터 접근을 조정합니다.

- 구성 요소: Service 클래스 (ApplicationsService, AuthService, JobService 등)

#### 데이터 접근 계층

- 설명: 데이터베이스와 상호작용하며 CRUD 작업을 수행합니다.

- 구성 요소: Repository 인터페이스 (AccountRepository, ApplicationRepository 등)

#### 도메인 계층

- 설명: 비즈니스 도메인 모델을 정의합니다.

- 구성 요소: 엔티티 클래스 (Account, Application, Bookmark, JobPosting 등)

#### 구성 계층

- 설명: 보안 및 애플리케이션 설정을 관리합니다.

- 구성 요소: SecurityConfig, JsonWebTokenProvider, CorsConfig 등
