package org.orderhub.pr.system.exception.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionMessage {
    public static final String USERNAME_MESSAGE = "아이디는 알파벳 소문자와 숫자를 사용하며, 숫자로 시작할 수 없습니다. (3~30자리)";
    public static final String PASSWORD_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 모두 사용하여 8자리 이상 100자리 이하입니다.";
    public static final String PHONE_NUMBER_MESSAGE = "올바른 전화번호 형식이 아닙니다.";
}
