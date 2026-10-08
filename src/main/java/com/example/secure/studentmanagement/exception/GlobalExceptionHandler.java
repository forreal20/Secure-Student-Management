package com.example.secure.studentmanagement.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "Validation Failed");
        body.put("messages", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatusException(
            ResponseStatusException exception) {

        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", exception.getReason());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConflict(
            DataIntegrityViolationException exception) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put(
                "message",
                "The operation conflicts with existing database data"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put(
                "message",
                "An unexpected error occurred"
        );

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(body);
    }
}