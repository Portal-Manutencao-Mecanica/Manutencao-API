package com.weg.Maintenance_API.exception.handler;

import com.weg.Maintenance_API.exception.dto.ApiErrorResponse;
import com.weg.Maintenance_API.exception.type.ConflictException;
import com.weg.Maintenance_API.exception.type.ExpiredTokenException;
import com.weg.Maintenance_API.exception.type.InvalidRequestException;
import com.weg.Maintenance_API.exception.type.InvalidTokenException;
import com.weg.Maintenance_API.exception.type.NotificationDeliveryException;
import com.weg.Maintenance_API.exception.type.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
            InvalidRequestException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Valor de requisição inválido.",
                request
        );
    }

    @ExceptionHandler(NotificationDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationDelivery(
            NotificationDeliveryException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_GATEWAY,
                "NOTIFICATION_DELIVERY_FAILED",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler({InvalidTokenException.class, ExpiredTokenException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidToken(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNAUTHORIZED,
                exception instanceof ExpiredTokenException
                        ? "TOKEN_EXPIRED"
                        : "INVALID_TOKEN",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "A operação viola uma restrição de integridade. Revise os dados relacionados.",
                request
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(
            ObjectOptimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.CONFLICT,
                "CONCURRENT_UPDATE",
                "O registro foi alterado por outro usuário. Atualize os dados e tente novamente.",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                errorCode,
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                Map.of()
        );
        return ResponseEntity.status(status).body(response);
    }
}
