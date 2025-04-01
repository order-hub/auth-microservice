package org.orderhub.pr.jwt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.auth.repository.MemberQueryRepository;
import org.orderhub.pr.jwt.dto.AuthResponse;
import org.orderhub.pr.jwt.dto.LoginRequest;
import org.orderhub.pr.jwt.dto.LogoutRequest;
import org.orderhub.pr.jwt.dto.TokenRequest;
import org.orderhub.pr.jwt.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.orderhub.pr.system.exception.auth.ExceptionMessage.INVALID_USERNAME_PASSWORD_ERROR;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.MEMBER_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberQueryRepository memberQueryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(HttpServletResponse response, LoginRequest request) throws JsonProcessingException {
        // 사용자 조회
        Member member = memberQueryRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException(INVALID_USERNAME_PASSWORD_ERROR);
        }

        // JWT 발급 (쿠키 저장 방식)
        String accessToken = jwtService.generateAccessToken(response, member);
        String refreshToken = jwtService.generateRefreshToken(response, member);

        return new AuthResponse(accessToken, refreshToken);
    }

    public void logout(HttpServletResponse response, LogoutRequest request) {
        Member member = memberQueryRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));
        jwtService.logout(member, response);
    }

    public AuthResponse generateTokens(TokenRequest tokenRequest, HttpServletResponse response) throws JsonProcessingException {
        // 사용자 조회
        Member member = memberQueryRepository.findById(tokenRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND_ERROR));

        // JWT 발급 (액세스 토큰, 리프레시 토큰)
        String accessToken = jwtService.generateAccessToken(response, member);
        String refreshToken = jwtService.generateRefreshToken(response, member);

        // 토큰을 포함한 인증 응답 반환
        return new AuthResponse(accessToken, refreshToken);
    }

}
