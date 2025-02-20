package org.orderhub.pr.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String realName;

    @Column(nullable = false, unique = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MANAGER; // 기본값 MANAGER 설정

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus status = MemberStatus.PENDING;

    @CreationTimestamp
    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    @Builder.Default
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant updatedAt = Instant.now();
}
