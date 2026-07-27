package com.weg.Maintenance_API.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Executa a operacao deste metodo.
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
