package com.weg.Maintenance_API.auth.service;

import jakarta.servlet.http.HttpServletRequest;

public record ClientRequestMetadata(
        String endpoint,
        String httpMethod,
        String ipAddress,
        String userAgent
) {

    public static ClientRequestMetadata from(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ipAddress = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
        return new ClientRequestMetadata(
                request.getRequestURI(),
                request.getMethod(),
                ipAddress,
                request.getHeader("User-Agent")
        );
    }
}
