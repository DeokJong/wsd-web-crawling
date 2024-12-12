package com.wsd.web.wsd_web_crawling.common.domain;

import com.wsd.web.wsd_web_crawling.common.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {

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
}
