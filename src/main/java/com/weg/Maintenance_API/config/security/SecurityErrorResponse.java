package com.weg.Maintenance_API.config.security;

import java.time.LocalDateTime;

public record SecurityErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
