package com.weg.Maintenance_API.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

//    senha ou e-mail inválido no login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<SecurityErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                request
        );
    }

//    JWT ausente ou inválido em rota protegida
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<SecurityErrorResponse> handleDisabled(
            DisabledException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "User account is disabled",
                request
        );
    }
//  JWT válido, mas role insuficiente
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<SecurityErrorResponse> handleLocked(
            LockedException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                "User account is locked",
                request
        );
    }

    private ResponseEntity<SecurityErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        SecurityErrorResponse body = new SecurityErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(body);
    }
}