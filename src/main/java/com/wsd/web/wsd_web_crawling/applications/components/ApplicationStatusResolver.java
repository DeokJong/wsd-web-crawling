package com.wsd.web.wsd_web_crawling.applications.components;

import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;

/**
 * {@code ApplicationStatusResolver} 클래스는 한글 상태 문자열과 {@link ApplicationStatus} 열거형 간의 변환을 담당합니다.
 */
public class ApplicationStatusResolver {

    /**
     * 한글 상태 문자열을 {@link ApplicationStatus} 열거형으로 변환합니다.
     *
     * @param korean 변환할 한글 상태 문자열
     * @return 대응되는 {@link ApplicationStatus} 값, 일치하는 값이 없으면 {@link ApplicationStatus#UNKNOWN}
     */
    public static ApplicationStatus toStatus(String korean) {
        if (korean.equals("지원 완료")) {
            return ApplicationStatus.APPLIED;
        } else if (korean.equals("취소됨")) {
            return ApplicationStatus.CANCELLED;
        } else if (korean.equals("거절됨")) {
            return ApplicationStatus.REJECTED;
        } else if (korean.equals("면접 완료")) {
            return ApplicationStatus.INTERVIEW;
        } else if (korean.equals("채용 완료")) {
            return ApplicationStatus.HIRED;
        }
        return ApplicationStatus.UNKNOWN;
    }

    /**
     * {@link ApplicationStatus} 열거형을 한글 상태 문자열로 변환합니다.
     *
     * @param status 변환할 {@link ApplicationStatus} 값
     * @return 대응되는 한글 상태 문자열, 일치하는 값이 없으면 "알 수 없음"
     */
    public static String toKorean(ApplicationStatus status) {
        if (status == ApplicationStatus.APPLIED) {
            return "지원 완료";
        } else if (status == ApplicationStatus.CANCELLED) {
            return "취소됨";
        } else if (status == ApplicationStatus.REJECTED) {
            return "거절됨";
        } else if (status == ApplicationStatus.INTERVIEW) {
            return "면접 완료";
        } else if (status == ApplicationStatus.HIRED) {
            return "채용 완료";
        }
        return "알 수 없음";
    }
}
