package org.orderhub.pr.jwt.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.auth.domain.Member;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtGenerator {
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

//    public String generateAccessToken(final PrivateKey privateKey, final long ACCESS_EXPIRATION, Member member) throws JsonProcessingException {
//        long now = System.currentTimeMillis();
//
//        return Jwts.builder()
//                .setHeader(HEADER)
//                .setClaims(createClaims(member))
//                .setSubject(String.valueOf(member.getId()))
//                .setExpiration(new Date(now + ACCESS_EXPIRATION))
//                .signWith(privateKey, SignatureAlgorithm.RS256)
//                .compact();
//    }

    public String generateAccessToken(PrivateKey privateKey, long expiration, Member member) throws JsonProcessingException {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expiration);

        String jwtId = UUID.randomUUID().toString(); // JWT ID를 고유하게 생성

        return Jwts.builder()
                .setHeader(HEADER)
                .setClaims(createClaims(member))
                .setSubject(member.getId().toString())
                .setIssuedAt(Date.from(now))  // 발급 시간
                .setExpiration(Date.from(expirationTime))  // 만료 시간
                .setId(jwtId)  // jti 추가
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(final PrivateKey privateKey, final long REFRESH_EXPIRATION, Member member) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setHeader(HEADER)
                .setSubject(String.valueOf(member.getId()))
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // 헤더 생성
//    private Map<String, Object> createHeader() {
//        Map<String, Object> header = new HashMap<>();
//        header.put("typ", "JWT");
//        header.put("alg", "RS256");
//        return header;
//    }

    // 상수로 변경
    private static final Map<String, Object> HEADER = Map.of(
            "typ", "JWT",
            "alg", "RS256"
    );

//    // 페이로드 생성
//    private Map<String, Object> createClaims(Member member) throws JsonProcessingException {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("memberId", member.getId());
//        claims.put("username", member.getUsername());
//        claims.put("realName", member.getRealName());
//        claims.put("role", member.getRole().name());
//        claims.put("status", member.getStatus().name());
//
//        String createdAtString = null;
//        if (member.getCreatedAt() != null) {
//            createdAtString = objectMapper.writeValueAsString(member.getCreatedAt());
//            if (createdAtString != null) {
//                createdAtString = createdAtString.replace("\"", "");
//            }
//        }
//
//        if (createdAtString == null) {
//            createdAtString = "N/A";
//        }
//
//        claims.put("createdAt", createdAtString);
//        return claims;
//    }

private Map<String, Object> createClaims(Member member) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("memberId", member.getId());
    claims.put("username", member.getUsername());
    claims.put("realName", member.getRealName());
    claims.put("role", member.getRole().name());
    claims.put("status", member.getStatus().name());

    String createdAtString = member.getCreatedAt() != null
            ? member.getCreatedAt().toString()  // createdAt을 String으로 변환
            : "N/A";  // 값이 없으면 "N/A"로 설정

    claims.put("createdAt", createdAtString);
    return claims;
}
}