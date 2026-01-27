package com.redirecionador.redirecionador.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(SlugNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSlugNotFound(SlugNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("slug_not_found", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(InvalidRedirectUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRedirectUrl(InvalidRedirectUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("invalid_redirect_url", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSlugAlreadyExists(SlugAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("slug_already_exists", ex.getMessage(), Instant.now()));
    }

    public record ErrorResponse(String error, String message, Instant timestamp) {
    }
}
