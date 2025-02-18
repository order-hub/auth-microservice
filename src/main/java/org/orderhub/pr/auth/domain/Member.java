package org.orderhub.pr.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private UUID id;

    private String username;

    private String password;

    private String realName;

    private String tel;

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.MANAGER; // 기본값 MANAGER 설정

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    protected Member(String username, String password, String realName, String tel, MemberRole role, MemberStatus status) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.tel = tel;
        this.role = role;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
