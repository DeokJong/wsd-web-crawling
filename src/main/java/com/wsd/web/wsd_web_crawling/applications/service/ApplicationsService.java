package com.wsd.web.wsd_web_crawling.applications.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationDTO;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet.ApplicationGetRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationPost.ApplicationPostRequest;
import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.model.DateOrder;
import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.ApplicationRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationsService {
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final AccountRepository accountRepository;
    private final JsonWebTokenProvider jsonWebTokenProvider;

    @Transactional
    public Response<?> addApplication(ApplicationPostRequest request, HttpServletRequest httpServletRequest) {
      Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
      Account account = accountRepository.findByUsername(username.get()).orElse(null);
        
        if (request.getPostingId() == null) {
          return Response.createResponseWithoutData(HttpStatus.OK.value(), "존재하지 않는 구인 정보입니다.");
        }

        if (account == null) {
          return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        // 중복 지원 체크
        if (applicationRepository.findByAccountAndJobPostingId(account, request.getPostingId()).isPresent()) {
          return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "이미 지원한 공고입니다.");
        }
        
        Application application = Application.builder()
            .account(account)
            .jobPosting(jobPostingRepository.findById(request.getPostingId()).orElse(null))
            .appliedAt(LocalDateTime.now())
            .status(ApplicationStatus.APPLIED)
            .build();
        
        applicationRepository.save(application);

        ApplicationDTO response = ApplicationDTO.builder()
            .applicationId(application.getId())
            .appliedAt(application.getAppliedAt())
            .status(application.getStatus().name())
            .jobPostingId(application.getJobPosting().getId())
            .jobPostingTitle(application.getJobPosting().getTitle())
            .jobPostingCompany(application.getJobPosting().getCompany())
            .jobPostingLink(application.getJobPosting().getLink())
            .build();
        
        return Response.createResponse(HttpStatus.OK.value(), "지원 추가 성공", response);
    }
    
    @Transactional(readOnly = true)
    public Response<?> getApplications(ApplicationGetRequest request, HttpServletRequest httpServletRequest) {
        Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
        Account account = accountRepository.findByUsername(username.get()).orElse(null);
        List<Application> applications = applicationRepository.findByAccount(account);

        if (account == null) {
          return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        if (request.getStatus() != null) {
          applications = applications.stream()
              .filter(application -> application.getStatus().equals(request.getStatus()))
              .collect(Collectors.toList());
        }

        applications.sort(Comparator.comparing(Application::getAppliedAt, request.getDateOrder().equals(DateOrder.ASC) ? Comparator.naturalOrder() : Comparator.reverseOrder()));

        List<ApplicationDTO> response = applications.stream()
            .map(application -> ApplicationDTO.builder()
                .applicationId(application.getId())
                .appliedAt(application.getAppliedAt())
                .status(application.getStatus().name())
                .jobPostingId(application.getJobPosting().getId())
                .jobPostingTitle(application.getJobPosting().getTitle())
                .jobPostingCompany(application.getJobPosting().getCompany())
                .jobPostingLink(application.getJobPosting().getLink())
                .build())
            .collect(Collectors.toList());

        return Response.createResponse(HttpStatus.OK.value(), "지원 내역 조회 성공", response);
    }
    
    @Transactional
    public Response<?> deleteApplication(Long applicationId, HttpServletRequest httpServletRequest) {
        Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
        Account account = accountRepository.findByUsername(username.get()).orElse(null);
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (account == null) {
          return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        if (!application.getAccount().getId().equals(account.getId()) && application != null) {
          return Response.createResponseWithoutData(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");
        }

        if (application == null) {
          return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "지원 내역을 찾을 수 없습니다.");
        }

        application.setStatus(ApplicationStatus.CANCELLED);


        return Response.createResponseWithoutData(HttpStatus.OK.value(), "지원 취소 성공");
    }
}
