package org.orderhub.pr.jwt.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderhub.pr.auth.domain.Member;
import org.orderhub.pr.system.exception.auth.BusinessException;
import org.orderhub.pr.jwt.repository.RedisTokenRepository;
import org.orderhub.pr.jwt.domain.TokenStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;

import static org.orderhub.pr.jwt.security.JwtRule.*;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.INVALID_JWT;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.JWT_TOKEN_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JwtService {
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTokenRepository redisTokenRepository;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${jwt.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    public JwtService(CustomUserDetailsService customUserDetailsService, JwtGenerator jwtGenerator,
                      JwtUtil jwtUtil, RedisTokenRepository redisTokenRepository,
                      RsaKeyLoader rsaKeyLoader) throws Exception {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.redisTokenRepository = redisTokenRepository;
        this.privateKey = rsaKeyLoader.loadPrivateKey();
        this.publicKey = rsaKeyLoader.loadPublicKey();
    }

    public String generateAccessToken(HttpServletResponse response, Member requestMember) throws JsonProcessingException {
        String accessToken = jwtGenerator.generateAccessToken(privateKey, ACCESS_EXPIRATION, requestMember);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return accessToken;
    }

    @Transactional
    public String generateRefreshToken(HttpServletResponse response, Member requestMember) {
        String refreshToken = jwtGenerator.generateRefreshToken(privateKey, REFRESH_EXPIRATION, requestMember);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        redisTokenRepository.saveRefreshToken(requestMember.getId(), refreshToken);
        return refreshToken;
    }

    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(JWT_TOKEN_NOT_FOUND);
        }
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    public String resolveTokenFromHeaderOrCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        // 1. 먼저 Authorization 헤더에서 토큰 확인 (엑세스 토큰의 경우)
        String headerToken = getTokenFromHeader(request, tokenPrefix);
        if (headerToken != null) {
            return headerToken;
        }

        // 2. 헤더에 토큰이 없으면 쿠키에서 토큰을 확인
        return resolveTokenFromCookie(request, tokenPrefix);
    }

    // 헤더에서 토큰을 확인하는 메서드 (엑세스 토큰만 처리)
    private String getTokenFromHeader(HttpServletRequest request, JwtRule tokenPrefix) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length()).trim();
        }
        return null;
    }

    public boolean validateAccessToken(String token) {
        TokenStatus tokenStatus = jwtUtil.getTokenStatus(token);
        log.debug("Validating access token {}", tokenStatus);
        return tokenStatus == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, UUID memberId) {
        // Step 1: JWT 토큰 유효성 검사
        boolean isRefreshValid = jwtUtil.getTokenStatus(token) == TokenStatus.AUTHENTICATED;

        // Step 2: Redis에서 리프레시 토큰 조회
        Optional<String> storedTokenOpt = redisTokenRepository.getRefreshToken(memberId);
        // Step 3: 토큰이 Redis에 존재하고, 요청받은 토큰과 일치하는지 확인
        boolean isTokenMatched = storedTokenOpt.isPresent() && storedTokenOpt.get().equals(token);

        // Step 4: 유효성 검사 통과 && 토큰 매칭 여부가 true일 경우에만 유효한 리프레시 토큰으로 간주
        return isRefreshValid && isTokenMatched;
    }

    public Authentication getAuthentication(String token) {
        UserDetails principal = customUserDetailsService.loadUserByUsername(getUserPk(token, publicKey));
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private String getUserPk(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public UUID getIdentifierFromRefresh(String refreshToken) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
            return UUID.fromString(subject);
        } catch (Exception e) {
            throw new BusinessException(INVALID_JWT);
        }
    }

    public void logout(Member member, HttpServletResponse response) {
        redisTokenRepository.deleteTokens(member.getId());

        Cookie accessCookie = jwtUtil.resetToken(ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtil.resetToken(REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}