package com.wsd.web.wsd_web_crawling.common.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wsd.web.wsd_web_crawling.common.domain.base.BaseTimeEntity;
import com.wsd.web.wsd_web_crawling.common.model.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
public class Application extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "job_posting_id", referencedColumnName = "id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private JobPosting jobPosting;

    @Column
    private LocalDateTime appliedAt;

    @Column
    private ApplicationStatus status; // 지원 상태 (예: 지원 완료, 취소됨 등)
}
