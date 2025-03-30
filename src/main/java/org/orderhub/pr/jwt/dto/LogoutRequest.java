package org.orderhub.pr.jwt.dto;

import lombok.Getter;

@Getter
public class LogoutRequest {
    private String username;
    private String password;
}
