package com.weg.Maintenance_API.config.security;

import com.weg.Maintenance_API.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler({
            BadCredentialsException.class,
            DisabledException.class,
            LockedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Credenciais inválidas.",
                request
        );
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleCredentialsExpired(
            CredentialsExpiredException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "CREDENTIALS_EXPIRED",
                "A senha temporária expirou. Solicite novas credenciais.",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                error,
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                Map.of()
        );
        return ResponseEntity.status(status).body(body);
    }
}
