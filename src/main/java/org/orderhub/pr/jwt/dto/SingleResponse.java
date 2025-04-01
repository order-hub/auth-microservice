package org.orderhub.pr.jwt.dto;

public record SingleResponse<T>(
        String status,
        String message,
        T data)
{}