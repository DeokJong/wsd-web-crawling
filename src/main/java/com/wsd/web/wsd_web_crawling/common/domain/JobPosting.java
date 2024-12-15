package com.wsd.web.wsd_web_crawling.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wsd.web.wsd_web_crawling.common.domain.base.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
@EqualsAndHashCode(callSuper = false)
public class JobPosting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;   // 구인 제목

    @Column
    private String company; // 회사 이름

    @Column
    private String link;    // 구인 링크

    @Column(unique = true)
    private String uniqueIdentifier;  // 중복 체크용 고유 식별자

    @Column
    private String location;  // 지역

    @Column
    private String experience;  // 경력

    @Column
    private String education;   // 학력

    @Column
    private String employmentType;  // 고용 형태

    @Column
    private String deadline;  // 마감일

    @Column
    private String sector;  // 직무 분야

    @Column
    private String salary;  // 평균 연봉

    @Column
    @Builder.Default
    private Integer viewCount = 0;  // 조회수

    @ManyToMany(mappedBy = "jobPostings", fetch = FetchType.EAGER)
    private Set<Bookmark> bookmarks;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private Set<Application> applications = new HashSet<>();
}
  