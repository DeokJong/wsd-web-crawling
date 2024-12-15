package com.wsd.web.wsd_web_crawling.common.model;

/**
 * 지원서의 상태를 나타냅니다.
 */
public enum ApplicationStatus {
    /** 지원서가 제출되었습니다. */
    APPLIED,

    /** 지원서가 거부되었습니다. */
    REJECTED,

    /** 지원서가 면접 단계에 있습니다. */
    INTERVIEW,

    /** 지원서가 검토 중입니다. */
    UNDER_REVIEW,

    /** 지원자가 채용되었습니다. */
    HIRED,

    /** 지원서가 취소되었습니다. */
    CANCELLED,

    /** 지원서 상태를 알 수 없습니다. */
    UNKNOWN,

    /** 모든 지원서 상태를 포함합니다. */
    ALL
}
