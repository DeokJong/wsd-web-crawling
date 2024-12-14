package com.wsd.web.wsd_web_crawling.applications.components;

import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;

public class ApplicationStatusResolver {

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
