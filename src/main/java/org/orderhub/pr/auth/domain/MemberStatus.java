package org.orderhub.pr.auth.domain;

public enum MemberStatus {
    ACTIVE,     // 모든 서비스를 이용할 수 있는 정상적인 회원 상태
    SUSPENDED,  // 일시적으로 서비스 이용이 제한된 상태
    DELETED,    // 회원 정보가 삭제되거나 더 이상 서비스를 이용할 수 없는 상태
    PENDING,    // 가입 절차가 완료되지 않은 상태
    EXPIRED     // 회원 기간이 끝난 상태
}
