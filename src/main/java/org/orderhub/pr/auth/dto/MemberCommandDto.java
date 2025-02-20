package org.orderhub.pr.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.orderhub.pr.auth.domain.MemberStatus;

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
    public static class ChangeStatusRequest {
        private MemberStatus memberStatus;
    }

    @Builder
    public static class ChangeStatusResponse {
        Boolean success;
    }

    @Builder
    public static class UpdateMemberRoleResponse {
        Boolean success;
    }

    @Builder
    public static class UpdatePasswordResponse {
        Boolean success;
    }

    @Builder
    public static class DeleteMemberResponse {
        Boolean success;
    }
}
