package org.orderhub.pr.jwt.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
