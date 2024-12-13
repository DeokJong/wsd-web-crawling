package com.wsd.web.wsd_web_crawling.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
  
    private static final String BASE_PACKAGE = "com.wsd.web.wsd_web_crawling";

    @Around("execution(* com.wsd.web.wsd_web_crawling..*.repository..*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.trace("[start - Repository] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.trace("[end - Repository] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }

    @Around("execution(* com.wsd.web.wsd_web_crawling..*.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.debug("[start - Service] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.debug("[end - Service] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }

    @Around("execution(* com.wsd.web.wsd_web_crawling..*.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.debug("[start - Controller] - {}", formatSignature(joinPoint));
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.info("[end - Controller] - {}, 실행 시간 : {}ms", formatSignature(joinPoint), endTime - startTime);

        return result;
    }
    
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

