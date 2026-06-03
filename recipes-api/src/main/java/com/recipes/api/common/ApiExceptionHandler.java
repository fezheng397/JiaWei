package com.recipes.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiError> handleConflict(ConflictException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.now(exception.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiError> handleNotFound(NotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.now(exception.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException exception) {
    return ResponseEntity.badRequest().body(ApiError.now(exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .orElse("Request validation failed");

    return ResponseEntity.badRequest().body(ApiError.now(message));
  }
}
