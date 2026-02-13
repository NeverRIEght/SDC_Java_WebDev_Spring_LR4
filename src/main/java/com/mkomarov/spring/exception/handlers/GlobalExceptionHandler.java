package com.mkomarov.spring.exception.handlers;

import com.mkomarov.spring.exception.classes.*;
import com.mkomarov.spring.model.dto.CommonResponse;
import com.mkomarov.spring.model.dto.PaginatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CommonResponse.error(e.getMessage())
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<CommonResponse<?>> handleInvalidRequestException(InvalidRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponse.error(e.getMessage())
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<?>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<CommonResponse<?>> handleServiceUnavailableException(ServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(CommonResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<CommonResponse<?>> handleExternalApiException(ExternalApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(CommonResponse.error("External API error: " + e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponse.error("Validation failed: " + errorMessage)
        );
    }

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<PaginatedResponse<?>> handlePaginationException(InvalidPaginationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                PaginatedResponse.error(e.getMessage(), e.getPage(), e.getPageSize())
        );
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> handleConstraintViolation(jakarta.validation.ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponse.error("Parameter validation failed: " + e.getMessage())
        );
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<CommonResponse<?>> handleResourceConflictException(ResourceConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                CommonResponse.error(e.getMessage())
        );
    }
}
