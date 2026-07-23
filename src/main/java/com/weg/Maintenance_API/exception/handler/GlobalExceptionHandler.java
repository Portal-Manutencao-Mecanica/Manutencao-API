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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Centraliza o tratamento das exceptions lançadas pelos controllers.
 *
 * O objetivo é evitar que cada controller precise montar respostas
 * diferentes para erros semelhantes.
 *
 * O @RestControllerAdvice faz com que esta classe seja aplicada
 * globalmente a todos os controllers da aplicação.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

//  Trata erros em DTOs com @Valid.
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
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value",
                        (firstMessage, secondMessage) -> firstMessage,
                        LinkedHashMap::new
                ));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                errors
        );
    }

//  Trata validações em parâmetros, query params e path variables.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation
                                .getPropertyPath()
                                .toString(),
                        violation -> violation.getMessage(),
                        (firstMessage, secondMessage) -> firstMessage,
                        LinkedHashMap::new
                ));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                errors
        );
    }

//  Trata JSON inválido e enums incorretos.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid",
                request.getRequestURI(),
                Map.of()
        );
    }

//  Trata parâmetros obrigatórios ausentes.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = Map.of(
                exception.getParameterName(),
                "Parameter is required"
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Required parameter is missing",
                request.getRequestURI(),
                errors
        );
    }

//  É o fallback para erros inesperados. Ele evita expor stack trace ou detalhes internos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI(),
                Map.of()
        );
    }


//  Monta a resposta padronizada utilizada pelos métodos acima.

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> errors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
