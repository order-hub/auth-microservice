package org.orderhub.pr.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.service.MemberQueryService;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import static org.orderhub.pr.config.SecurityConfig.PERMITTED_URI;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final MemberQueryService memberQueryService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 허용된 URI는 인증을 거치지 않음
        if (isPermittedURI(request.getRequestURI())) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }

        // 엑세스 토큰을 헤더나 쿠키에서 찾음
        String accessToken = jwtService.resolveTokenFromHeaderOrCookie(request, JwtRule.ACCESS_PREFIX);

        // 엑세스 토큰이 유효하면 인증 정보 설정
        if (jwtService.validateAccessToken(accessToken)) {
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 엑세스 토큰이 만료된 경우 리프레시 토큰을 사용하여 새 엑세스 토큰을 발급
        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
        Member member = findUserByRefreshToken(refreshToken);

        if (jwtService.validateRefreshToken(refreshToken, member.getId())) {
            String reissuedAccessToken = jwtService.generateAccessToken(response, member);
            jwtService.generateRefreshToken(response, member);

            setAuthenticationToContext(reissuedAccessToken);
            filterChain.doFilter(request, response);
            return;
        }

        jwtService.logout(member, response);
    }

    private boolean isPermittedURI(String requestURI) {
        if (requestURI.equals("/api/auth")) {
            return false;
        }

        return Arrays.stream(PERMITTED_URI)
                .anyMatch(permitted -> {
                    String replace = permitted.replace("*", "");
                    return requestURI.contains(replace) || replace.contains(requestURI);
                });
    }


    private Member findUserByRefreshToken(String refreshToken) {
        UUID identifier = jwtService.getIdentifierFromRefresh(refreshToken);
        return memberQueryService.findMemberEntityById(identifier);
    }

    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}