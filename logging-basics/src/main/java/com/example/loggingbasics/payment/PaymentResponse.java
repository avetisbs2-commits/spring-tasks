package com.example.loggingbasics.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String maskedCard,
        String declineReason
) {}
