package com.alemandan.crm.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path, String code, Map<String, String> fieldErrors) {
        ApiError body = ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .code(code)
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), "not_found", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // Si hay múltiples errores por campo, se quedará el último; suficiente para un resumen
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        String message = "Validation failed";
        return build(HttpStatus.BAD_REQUEST, message, req.getRequestURI(), "validation_error", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String field = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "value";
            errors.put(field, v.getMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Constraint violation", req.getRequestURI(), "validation_error", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", req.getRequestURI(), "bad_request", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        // Mensaje genérico para no filtrar detalles de base de datos
        return build(HttpStatus.CONFLICT, "Resource conflict", req.getRequestURI(), "conflict", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        // En producción, logear ex con stacktrace; mensaje al cliente genérico
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI(), "internal_error", null);
    }
}