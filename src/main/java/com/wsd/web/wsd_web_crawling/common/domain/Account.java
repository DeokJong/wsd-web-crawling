package com.wsd.web.wsd_web_crawling.common.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wsd.web.wsd_web_crawling.common.domain.base.BaseTimeEntity;
import com.wsd.web.wsd_web_crawling.common.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
@EqualsAndHashCode(callSuper = false)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;   // 사용자 닉네임

    @Column(nullable = false, unique = true)
    private String username;   // 사용자 이름 (고유)

    @Column(nullable = false)
    private String password;   // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;  // 사용자 역할

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bookmark bookmark;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private Set<Application> applications = new HashSet<>();
}
