package com.wsd.web.wsd_web_crawling.applications.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wsd.web.wsd_web_crawling.authentication.service.AuthService;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.domain.JobPosting;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.repository.ApplicationRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationsService {
    
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final AuthService authService;

    @Transactional
    public Response<?> addApplication(Long jobPostingId, String resume) {
        Account account = authService.getCurrentAccount();
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 구인 정보입니다."));
        
        // 중복 지원 체크
        if (applicationRepository.findByAccountAndJobPosting(account, jobPosting).isPresent()) {
            throw new RuntimeException("이미 지원한 공고입니다.");
        }
        
        Application application = Application.builder()
            .account(account)
            .jobPosting(jobPosting)
            .resume(resume)
            .status("지원 완료")
            .build();
        
        applicationRepository.save(application);
        
        return Response.createResponseWithoutData(HttpStatus.OK.value(), "지원 추가 성공");
    }
    
    @Transactional(readOnly = true)
    public Response<?> getApplications(String status, String sort) {
        Account account = authService.getCurrentAccount();
        List<Application> applications = applicationRepository.findByAccount(account);
        
        // 상태별 필터링
        if (status != null && !status.isEmpty()) {
            applications = applications.stream()
                .filter(app -> app.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }
        
        // 날짜별 정렬
        if ("asc".equalsIgnoreCase(sort)) {
            applications.sort(Comparator.comparing(Application::getCreatedAt));
        } else if ("desc".equalsIgnoreCase(sort)) {
            applications.sort(Comparator.comparing(Application::getCreatedAt, Comparator.reverseOrder()));
        }
        return Response.createResponseWithoutData(HttpStatus.OK.value(), "지원 내역 조회 성공");
    }
    
    @Transactional
    public Response<?> deleteApplication(Long applicationId) {
        Account account = authService.getCurrentAccount();
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("지원 내역을 찾을 수 없습니다."));
        
        // 사용자 권한 확인
        if (!application.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        
        // 취소 가능 여부 확인 (예: 상태가 '지원 완료'인 경우에만 취소 가능)
        if (!"지원 완료".equalsIgnoreCase(application.getStatus())) {
            throw new RuntimeException("취소할 수 없는 상태입니다.");
        }
        
        application.setStatus("취소됨");
        applicationRepository.save(application);
        
        return Response.createResponseWithoutData(HttpStatus.OK.value(), "지원 취소 성공");
    }
}
