package com.wsd.web.wsd_web_crawling.common.domain;

import com.wsd.web.wsd_web_crawling.jobs.dto.JobRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

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
    private int viewCount;  // 조회수

    @ManyToMany(mappedBy = "jobPostings", fetch = FetchType.EAGER)
    private Set<Bookmark> bookmarks;

    public void update(JobRequest request) {
        this.title = request.getTitle();
        this.company = request.getCompany();
        this.link = request.getLink();
        this.location = request.getLocation();
        this.experience = request.getExperience();
        this.education = request.getEducation();
        this.employmentType = request.getEmploymentType();
        this.deadline = request.getDeadline();
        this.sector = request.getSector();
        this.salary = request.getSalary();
    }
}
