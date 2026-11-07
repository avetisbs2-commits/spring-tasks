package com.example.loggingbasics.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/** COMPLETE: the card number and email are sensitive and must never appear in logs. */
public record PaymentRequest(
        @NotBlank @Email String customerEmail,
        @NotBlank @Pattern(regexp = "\\d{16}", message = "must be 16 digits") String cardNumber,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter code like AMD") String currency
) {}
