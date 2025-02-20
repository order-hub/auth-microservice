package org.orderhub.pr.auth.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExceptionMessage {
    public static final String DUPLICATE_EMAIL_ERROR = "이미 사용중인 이메일입니다. 새로운 이메일을 입력해주세요.";
    public static final String DUPLICATE_USERNAME_ERROR = "이미 사용중인 아이디입니다. 새로운 아이디를 입력해주세요.";
    public static final String NOT_VALID_TOKEN_ERROR = "유효하지 않은 토큰 입니다.";
    public static final String INVALID_USERNAME_PASSWORD_ERROR = "이메일 또는 비밀번호가 잘못되었습니다.";
    public static final String INVALID_PASSWORD_ERROR = "비밀번호가 맞지 않습니다.";
    public static final String INVALID_CHK_PASSWORD_NEW_PASSWORD_ERROR = "신규 비밀번호와 확인 비밀번호가 서로 다릅니다.";
    public static final String EMAIL_NOT_VERIFIED_ERROR = "이메일이 인증되지 않은 회원입니다.";


    public static final String MEMBER_NOT_FOUND_ERROR = "멤버를 찾을 수 없습니다.";
    public static final String INVALID_USER_ERROR = "유효하지 않은 멤버입니다.";
    public static final String UNAUTHORIZED_ACCESS_ERROR = "유효하지 않은 접근입니다. 해당 멤버에게는 권한이 없습니다.";
    public static final String NO_AUTHENTICATION_IN_SECURITY_CONTEXT_ERROR = "인증되지 않은 사용자 접근 입니다.";
    public static final String MEMBER_BLOCKED_ERROR = "계정이 차단되었습니다. 자세한 내용은 관리자에게 문의하세요.";
}
