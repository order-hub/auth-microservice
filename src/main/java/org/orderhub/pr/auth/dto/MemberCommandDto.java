package org.orderhub.pr.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.auth.domain.MemberStatus;

import java.util.UUID;

import static org.orderhub.pr.policy.Accounts.Validation.*;
import static org.orderhub.pr.system.exception.auth.ValidationMessage.*;

public class MemberCommandDto {

    @Data
    @Builder
    public static class SignUpRequest {
        @NotEmpty(message = "아이디는 필수입니다.")
        @Pattern(regexp = USERNAME, message = USERNAME_MESSAGE)
        private String username;

        @NotEmpty(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = PASSWORD, message = PASSWORD_MESSAGE)
        private String password;

        @NotEmpty(message = "실명은 필수입니다.")
        private String realName;

        @NotEmpty(message = "전화번호는 필수입니다.")
        @Pattern(regexp = PHONE_NUMBER, message = PHONE_NUMBER_MESSAGE)
        private String tel;

    }

    @Getter
    @Builder
    public static class SignUpResponse {
        Boolean success;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberStatusRequest {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        UUID targetId;
        MemberStatus status;
    }

    @Getter
    @Builder
    public static class UpdateMemberStatusResponse {
        Boolean success;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberRoleRequest {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        UUID targetId;
        MemberRole role;
    }

    @Getter
    @Builder
    public static class UpdateMemberRoleResponse {
        Boolean success;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePasswordRequest {
        @NotEmpty(message = "현재 비밀번호는 필수 입력값입니다.")
        String currentPassword;

        @NotEmpty(message = "새 비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = PASSWORD, message = PASSWORD_MESSAGE)
        String newPassword;

    }

    @Builder
    @Getter
    public static class UpdatePasswordResponse {
        Boolean success;
    }

    @Builder
    @Getter
    public static class DeleteMemberResponse {
        Boolean success;
    }
}
