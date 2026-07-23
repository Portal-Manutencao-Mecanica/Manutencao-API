package com.weg.Maintenance_API.exception.handler;

import com.weg.Maintenance_API.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null
                                ? "Valor inválido."
                                : error.getDefaultMessage(),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));
        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Existem campos inválidos.",
                request,
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ));
        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Existem parâmetros inválidos.",
                request,
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_BODY",
                "O corpo da requisição é inválido.",
                request,
                Map.of()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "MISSING_PARAMETER",
                "Um parâmetro obrigatório não foi informado.",
                request,
                Map.of(exception.getParameterName(), "O parâmetro é obrigatório.")
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "O identificador ou parâmetro informado é inválido.",
                request,
                Map.of(exception.getName(), "Valor incompatível com o tipo esperado.")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UNEXPECTED_ERROR",
                "Ocorreu um erro inesperado.",
                request,
                Map.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request,
            Map<String, String> errors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                errorCode,
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(status).body(response);
    }
}
