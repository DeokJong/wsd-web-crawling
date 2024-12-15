package com.wsd.web.wsd_web_crawling.applications.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationDTO;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationGet.ApplicationGetRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationPost.ApplicationPostRequest;
import com.wsd.web.wsd_web_crawling.applications.dto.ApplicationUpdateRequest.ApplicationUpdateRequest;
import com.wsd.web.wsd_web_crawling.authentication.components.JsonWebTokenProvider;
import com.wsd.web.wsd_web_crawling.common.domain.Account;
import com.wsd.web.wsd_web_crawling.common.dto.Response;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;
import com.wsd.web.wsd_web_crawling.common.domain.Application;
import com.wsd.web.wsd_web_crawling.common.repository.AccountRepository;
import com.wsd.web.wsd_web_crawling.common.repository.ApplicationRepository;
import com.wsd.web.wsd_web_crawling.common.repository.JobPostingRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationsService 클래스는 지원 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationsService {
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final AccountRepository accountRepository;
    private final JsonWebTokenProvider jsonWebTokenProvider;

    /**
     * 새로운 지원을 추가합니다.
     *
     * @param request 요청 DTO
     * @param httpServletRequest HTTP 요청 객체
     * @return 응답 객체
     */
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
            Application application = applicationRepository.findByAccountAndJobPostingId(account, request.getPostingId()).get();
            if (application.getStatus() != ApplicationStatus.CANCELLED) {
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "이미 지원한 공고입니다.");
            }
        }

        if (jobPostingRepository.findById(request.getPostingId()).orElse(null) == null) {
            return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 구인 정보입니다.");
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
        
        return Response.createResponse(HttpStatus.CREATED.value(), "지원 추가 성공", response);
    }
    
    /**
     * 사용자의 지원 내역을 조회합니다.
     *
     * @param httpServletRequest HTTP 요청 객체
     * @param request 요청 DTO
     * @return 응답 객체
     */
    @Transactional(readOnly = true)
    public Response<?> getApplications(HttpServletRequest httpServletRequest, ApplicationGetRequest request) {
        Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
        Account account = accountRepository.findByUsername(username.get()).orElse(null);
        Page<Application> pageApplications;
        
        if (request.getStatus() == ApplicationStatus.ALL) {
            pageApplications = applicationRepository.findByAccount(account, request.toPageable());
        } else {
            pageApplications = applicationRepository.findByAccountAndStatus(account, request.getStatus(), request.toPageable());
        }

        if (account == null) {
            return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        Page<ApplicationDTO> response = pageApplications.map(application -> ApplicationDTO.builder()
            .applicationId(application.getId())
            .appliedAt(application.getAppliedAt())
            .status(application.getStatus().name())
            .jobPostingId(application.getJobPosting().getId())
            .jobPostingTitle(application.getJobPosting().getTitle())
            .jobPostingCompany(application.getJobPosting().getCompany())
            .jobPostingLink(application.getJobPosting().getLink())
            .build());

        return Response.createResponse(HttpStatus.OK.value(), "지원 내역 조회 성공", response);
    }
    
    /**
     * 사용자의 특정 지원을 삭제(취소)합니다.
     *
     * @param applicationId 지원 ID
     * @param httpServletRequest HTTP 요청 객체
     * @return 응답 객체
     */
    @Transactional
    public Response<?> deleteApplication(Long applicationId, HttpServletRequest httpServletRequest) {
        Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
        Account account = accountRepository.findByUsername(username.get()).orElse(null);
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null) {
            return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "아직 아무것도 지원하지 않았습니다.");
        }

        if (account == null) {
            return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        if (!application.getAccount().getId().equals(account.getId()) && application != null) {
            return Response.createResponseWithoutData(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");
        }

        if (application == null) {
            return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "지원 내역을 찾을 수 없습니다.");
        }

        switch (application.getStatus()) {
            case CANCELLED:
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "이미 취소된 지원 내역입니다.");
            case INTERVIEW:
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "면접 중인 지원 내역입니다. 취소 할 수 없습니다.");
            case HIRED:
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "채용된 지원 내역입니다. 취소 할 수 없습니다.");
            case REJECTED:
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "거절된 지원 내역입니다. 취소 할 수 없습니다.");
            case UNDER_REVIEW:
                return Response.createResponseWithoutData(HttpStatus.CONFLICT.value(), "검토 중인 지원 내역입니다. 취소 할 수 없습니다.");
            case ALL:
                return Response.createResponseWithoutData(HttpStatus.UNPROCESSABLE_ENTITY.value(), "취소할 수 없는 상태의 지원 내역입니다.");
            default:
                break;
        }

        application.setStatus(ApplicationStatus.CANCELLED);

        return Response.createResponseWithoutData(HttpStatus.NO_CONTENT.value(), "지원 취소 성공");
    }

    /**
     * 사용자의 특정 지원 상태를 업데이트합니다.
     *
     * @param applicationId 지원 ID
     * @param requestDto 업데이트 요청 DTO
     * @param httpServletRequest HTTP 요청 객체
     * @return 응답 객체
     */
    public Response<?> updateApplication(
      Long applicationId,
      ApplicationUpdateRequest requestDto,
      HttpServletRequest httpServletRequest) {
        Optional<String> username = jsonWebTokenProvider.getUsernameFromRequest(httpServletRequest);
        Account account = accountRepository.findByUsername(username.get()).orElse(null);
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null) {
            return Response.createResponseWithoutData(HttpStatus.NOT_FOUND.value(), "지원 내역을 찾을 수 없습니다.");
        }

        if (account == null) {
            return Response.createResponseWithoutData(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다.");
        }

        if (!application.getAccount().getId().equals(account.getId()) && application != null) {
            return Response.createResponseWithoutData(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");
        }

        application.setStatus(requestDto.getStatus());

        return Response.createResponseWithoutData(HttpStatus.OK.value(), "지원 상태 변경 성공");
    }
}
