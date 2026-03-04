package com.soulside.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(JsonSerializationException.class)
    public ResponseEntity<Map<String, String>> handleJsonSerializationException(JsonSerializationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JSON format: " + ex.getCause().getMessage()));
    }
}
