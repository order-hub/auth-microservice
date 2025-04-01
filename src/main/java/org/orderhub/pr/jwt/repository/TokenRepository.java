package org.orderhub.pr.jwt.repository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository{
    // Refresh Token을 Redis에 저장
    void saveRefreshToken(UUID userId, String refreshToken);
//
//    // Access Token을 Redis에 저장
//    void saveAccessToken(UUID userId, String accessToken);

    // Refresh Token 조회
    Optional<String> getRefreshToken(UUID userId);

//    // Access Token 조회
//    Optional<String> getAccessToken(UUID userId);

    // 토큰 삭제
    void deleteTokens(UUID userId);
}
