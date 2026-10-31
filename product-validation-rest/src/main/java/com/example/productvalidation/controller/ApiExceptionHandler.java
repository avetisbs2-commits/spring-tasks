package com.example.productvalidation.controller;

import com.example.productvalidation.exception.DuplicateSkuException;
import com.example.productvalidation.exception.InvalidFilterException;
import com.example.productvalidation.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/** Validation and duplicate SKU are complete; students add the remaining errors. */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalidBody(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ProblemDetail> invalidFilter(InvalidFilterException exception) {
        // TODO: 400 ProblemDetail; use exception.getMessage() as detail.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> missingProduct(ProductNotFoundException exception) {
        // TODO: 404 ProblemDetail; use exception.getMessage() as detail.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<ProblemDetail> duplicateSku(DuplicateSkuException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }
}
