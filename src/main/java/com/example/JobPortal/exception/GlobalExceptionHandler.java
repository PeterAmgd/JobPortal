package com.example.JobPortal.exception;


import com.glassdoor.authentication.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom authentication exceptions
    @ExceptionHandler(CustomAuthenticationExceptions.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            CustomAuthenticationExceptions.AuthenticationException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), request, HttpStatus.UNAUTHORIZED);
    }

    // Handle user already exists exception
    @ExceptionHandler(CustomAuthenticationExceptions.UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            CustomAuthenticationExceptions.UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return buildErrorResponse(errors, request, HttpStatus.BAD_REQUEST);
    }

    // Handle missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildErrorResponse("Missing parameter: " + ex.getParameterName(), request, HttpStatus.BAD_REQUEST);
    }

    // Handle method not allowed
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildErrorResponse("Method not allowed: " + ex.getMethod(), request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse("Unexpected error occurred: " + ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Utility method to create error response
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpServletRequest request, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .errors(List.of(message))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(List<String> errors, HttpServletRequest request, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
