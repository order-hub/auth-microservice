package org.orderhub.pr.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    MANAGER("ROLE_MANAGER", "발주자"),     // 매니저
    BOSS("ROLE_BOSS", "점장"),            // 사장
    ADMIN("ROLE_ADMIN", "관리자");        // 본사
//    NOT_REGISTERED("ROLE_NONE","미가입자");

    private final String key;
    private final String title;
}
