package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.usecase.client.CreateClient.ClientAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return getErrorResponseResponseEntity(request, NOT_FOUND, "Resource not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedFound(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return getErrorResponseResponseEntity(request, METHOD_NOT_ALLOWED, "Method not allowed");
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleClientAlreadyExists(ClientAlreadyExistsException ex,
                                                                   HttpServletRequest request) {
        logger.warn("ClientAlreadyExists: {}", ex.getMessage());
        return getErrorResponseResponseEntity(request, CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        }
        return getErrorResponseResponseEntity(request, BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception", ex);
        return getErrorResponseResponseEntity(request, INTERNAL_SERVER_ERROR, ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(HttpServletRequest request, HttpStatus status, String message) {
        ErrorResponse body = ErrorResponse.basic(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(HttpServletRequest request, HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(status).body(body);
    }

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {
        public static ErrorResponse basic(Instant timestamp, int status, String error, String message, String path) {
            return new ErrorResponse(timestamp, status, error, message, path, null);
        }
    }
}
