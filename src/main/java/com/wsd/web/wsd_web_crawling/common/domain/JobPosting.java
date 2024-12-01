package com.wsd.web.wsd_web_crawling.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 구인 제목
     */
    @Column
    private String title;

    /**
     * 회사 이름
     */
    @Column
    private String company;

    /**
     * 구인 링크
     */
    @Column
    private String link;

    /**
     * 중복 체크를 위한 고유 식별자
     */
    @Column(unique = true)
    private String uniqueIdentifier;

    /**
     * 지역
     */
    @Column
    private String location;

    /**
     * 경력
     */
    @Column
    private String experience;

    /**
     * 학력
     */
    @Column
    private String education;

    /**
     * 고용 형태
     */
    @Column
    private String employmentType;

    /**
     * 마감일
     */
    @Column
    private String deadline;

    /**
     * 직무 분야
     */
    @Column
    private String sector;

    /**
     * 평균연봉
     */
    @Column
    private String salary;
}
