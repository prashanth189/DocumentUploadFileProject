package com.java.DocumentIngestion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<?> handleInvalidDocument(DocumentNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found ", ex.getMessage());
    }

    @ExceptionHandler(DocumentKeyNotFoundException.class)
    public ResponseEntity<?> handleInvalidDocument(DocumentKeyNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<?> handleInvalidDocument(InvalidDocumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<?> handleProcessingError(DocumentProcessingException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<?> handleProcessingError(InvalidPaginationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Pagination", ex.getMessage());
    }

    @ExceptionHandler(DatabaseAccessException.class)
    public ResponseEntity<?> handleProcessingError(DatabaseAccessException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Database Error", ex.getMessage());
    }

    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<?> handleProcessingError(InvalidSearchQueryException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Search Query", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleProcessingError(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("errorMessage", error);
        errorBody.put("message", message);
         return new ResponseEntity<>(errorBody, status);
    }
}
