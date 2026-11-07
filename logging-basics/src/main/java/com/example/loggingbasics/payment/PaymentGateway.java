package com.example.loggingbasics.payment;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * COMPLETE: a fake external provider. A card number that ends with 0000
 * simulates a provider outage, so you can see an ERROR log with a stack trace.
 */
@Component
public class PaymentGateway {
    public void charge(UUID paymentId, String cardNumber) {
        if (cardNumber.endsWith("0000")) {
            throw new PaymentGatewayException("Payment provider is unavailable",
                    new IOException("Connection timed out after 3000 ms"));
        }
    }
}
