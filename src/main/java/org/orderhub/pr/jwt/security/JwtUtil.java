package org.orderhub.pr.jwt.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.orderhub.pr.system.exception.auth.BusinessException;
import org.orderhub.pr.jwt.domain.TokenStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.Arrays;

import static org.orderhub.pr.system.exception.auth.ExceptionMessage.INVALID_EXPIRED_JWT;
import static org.orderhub.pr.system.exception.auth.ExceptionMessage.INVALID_JWT;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JwtUtil {
    private final PublicKey publicKey;

    public JwtUtil(RsaKeyLoader rsaKeyLoader) {
        try {
            this.publicKey = rsaKeyLoader.loadPublicKey();
        } catch (Exception e) {
            log.error("Failed to load public key", e);
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    // 토큰 상태 확인용
    public TokenStatus getTokenStatus(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.error(token, INVALID_EXPIRED_JWT);
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            log.error(token, INVALID_EXPIRED_JWT);
            throw new BusinessException(INVALID_JWT);
        }
    }

    // 쿠키에서 원하는 토큰을 찾는 역할
    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }

    public Cookie resetToken(JwtRule tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}