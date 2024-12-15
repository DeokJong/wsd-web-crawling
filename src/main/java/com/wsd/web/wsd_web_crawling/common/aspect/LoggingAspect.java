package com.wsd.web.wsd_web_crawling.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 로깅을 처리하는 Aspect 클래스입니다.
 * 리포지토리, 서비스, 컨트롤러 계층의 메서드 실행 시 로깅을 수행합니다.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {
  
    private static final String BASE_PACKAGE = "com.wsd.web.wsd_web_crawling";

    /**
     * 리포지토리 계층의 메서드 실행 전후로 로깅을 수행합니다.
     *
     * @param joinPoint 메서드 실행 지점
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외
     */
    @Around("execution(* com.wsd.web.wsd_web_crawling..*.repository..*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.trace("[start - Repository] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.trace("[end - Repository] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }

    /**
     * 서비스 계층의 메서드 실행 전후로 로깅을 수행합니다.
     *
     * @param joinPoint 메서드 실행 지점
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외
     */
    @Around("execution(* com.wsd.web.wsd_web_crawling..*.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.debug("[start - Service] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.debug("[end - Service] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }

    /**
     * 컨트롤러 계층의 메서드 실행 전후로 로깅을 수행합니다.
     *
     * @param joinPoint 메서드 실행 지점
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외
     */
    @Around("execution(* com.wsd.web.wsd_web_crawling..*.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.debug("[start - Controller] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.info("[end - Controller] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }
    
    /**
     * 메서드의 시그니처를 포맷하여 반환합니다.
     *
     * @param joinPoint 메서드 실행 지점
     * @return 포맷된 메서드 시그니처
     */
    private String formatSignature(ProceedingJoinPoint joinPoint) {
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        // 기본 패키지를 제거
        String relativeClassName = fullClassName.startsWith(BASE_PACKAGE) 
            ? fullClassName.substring(BASE_PACKAGE.length() + 1) 
            : fullClassName;
        
        return String.format("%s.%s", relativeClassName, methodName);
    }
}

