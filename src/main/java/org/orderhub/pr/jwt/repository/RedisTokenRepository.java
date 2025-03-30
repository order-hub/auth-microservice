package org.orderhub.pr.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveRefreshToken(UUID memberId, String refreshToken) {
        redisTemplate.opsForValue().set(memberId.toString() + ":refresh", refreshToken);
    }

//    @Override
//    public void saveAccessToken(UUID memberId, String accessToken) {
//        redisTemplate.opsForValue().set(memberId.toString()  + ":access", accessToken);
//    }

    @Override
    public Optional<String> getRefreshToken(UUID memberId) {
        String refreshToken = redisTemplate.opsForValue().get(memberId.toString()  + ":refresh");
        return Optional.ofNullable(refreshToken);
    }

//    @Override
//    public Optional<String> getAccessToken(UUID memberId) {
//        String accessToken = redisTemplate.opsForValue().get(memberId.toString()  + ":access");
//        return Optional.ofNullable(accessToken);
//    }

    @Override
    public void deleteTokens(UUID memberId) {
        redisTemplate.delete(memberId.toString()  + ":access");
        redisTemplate.delete(memberId.toString()  + ":refresh");
    }
}
