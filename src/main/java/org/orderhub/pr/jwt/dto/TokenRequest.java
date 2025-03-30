package org.orderhub.pr.jwt.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TokenRequest {
    private final UUID id;

    // 생성자, getter, setter
    public TokenRequest(UUID identifier) {
        this.id = identifier;
    }

}
