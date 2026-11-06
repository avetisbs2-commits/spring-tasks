package com.example.loggingbasics.web;

import com.example.loggingbasics.payment.PaymentGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** COMPLETE EXAMPLE: a client mistake is WARN, and we log field names, never the rejected values. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalidBody(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        List<String> fields = List.copyOf(errors.keySet());
        log.warn("Request validation failed: fields={}", fields);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    /** STUDENT TASK 4: the response is complete; add the log line. */
    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ProblemDetail> gatewayFailure(PaymentGatewayException exception) {
        // TODO (ERROR): log "Payment provider call failed" and pass the exception as the
        // last argument, so Logback prints the stack trace and its cause.

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, "Payment provider is unavailable, please try again later");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(problem);
    }
}
