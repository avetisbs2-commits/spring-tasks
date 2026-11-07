package com.example.loggingbasics.payment;

/** Thrown when the external payment provider cannot be reached. */
public class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
