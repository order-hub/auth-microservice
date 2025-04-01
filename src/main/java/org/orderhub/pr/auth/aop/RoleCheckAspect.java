package org.orderhub.pr.auth.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.domain.MemberRole;
import org.orderhub.pr.system.exception.auth.UnauthorizedException;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.orderhub.pr.system.exception.auth.ExceptionMessage.UNAUTHORIZED_ACCESS_ERROR;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {
    private final MemberQueryService memberQueryService;

    @Before("@annotation(org.orderhub.pr.auth.aop.annotation.AdminOnly)")
    public void checkAdminRole(JoinPoint joinPoint) {
        Object[] args;
        args = joinPoint.getArgs();
        UUID memberId = (UUID) args[0]; // 첫 번째 인자가 요청자의 ID라고 가정

        // 실제 회원 정보 조회 (의존성 주입 필요)
        Member member = memberQueryService.findMemberEntityById(memberId);

        if (member.getRole() != MemberRole.ADMIN) {
            throw new UnauthorizedException(UNAUTHORIZED_ACCESS_ERROR);
        }
    }
}
