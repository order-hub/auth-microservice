package org.orderhub.common.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.orderhub.common.MemberRole;
import org.orderhub.common.MemberStatus;

import java.time.Instant;
import java.util.UUID;

public class KafkaDto {

    @Data
    @Builder
    public static class SignUpSendMessage{
        private UUID memberId;
        private String username;
        private String password;
        private String realName;
        private String tel;
        private MemberRole role;
        private MemberStatus status;

        @CreationTimestamp
        @Builder.Default
        @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
        private Instant createdAt = Instant.now();

        @UpdateTimestamp
        @Builder.Default
        @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
        private Instant updatedAt = Instant.now();
    }

    @Data
    @Builder
    public static class UpdateStatusSendMessage{
        private UUID memberId;
        private MemberStatus status;
    }

    @Data
    @Builder
    public static class UpdateRoleSendMessage{
        private UUID memberId;
        private MemberRole role;
    }

    @Data
    @Builder
    public static class UpdatePasswordSendMessage{
        private UUID memberId;
        private String password;
    }

    @Data
    @Builder
    public static class DeleteMemberSendMessage{
        private UUID memberId;
    }

}
