package org.orderhub.pr.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.orderhub.common.MemberRole;
import org.orderhub.common.MemberStatus;
import org.springframework.http.HttpStatus;

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
    public static class SimpleSuccessResponse {
        private Boolean success;
    }


    @Getter
    @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private HttpStatus status;
        private T data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberStatusRequest {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        UUID targetId;
        MemberStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberRoleRequest {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        UUID targetId;
        MemberRole role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePasswordRequest {
        @NotEmpty(message = "대상 id를 입력해주세요")
        UUID targetId;

        @NotEmpty(message = "현재 비밀번호는 필수 입력값입니다.")
        String currentPassword;

        @NotEmpty(message = "새 비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = PASSWORD, message = PASSWORD_MESSAGE)
        String newPassword;

    }
}
