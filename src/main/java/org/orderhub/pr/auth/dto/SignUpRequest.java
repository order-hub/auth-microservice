    package org.orderhub.pr.auth.dto;

    import jakarta.validation.constraints.NotEmpty;
    import jakarta.validation.constraints.Pattern;
    import lombok.Builder;
    import lombok.Data;

    import static org.orderhub.pr.policy.Accounts.Validation.*;
    import static org.orderhub.pr.system.exception.auth.ExceptionMessage.*;

    @Data
    @Builder
    public class SignUpRequest {
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
