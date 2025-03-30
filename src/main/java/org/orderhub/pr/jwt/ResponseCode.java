package org.orderhub.pr.jwt;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("200", "Success");

    private final String status;
    private final String message;

    ResponseCode(String status, String message) {
        this.status = status;
        this.message = message;
    }
}