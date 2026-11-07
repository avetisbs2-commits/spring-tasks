package com.example.loggingbasics.payment;

import com.example.loggingbasics.logsafe.SensitiveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * STUDENT TASK 2: the business logic is complete, but nothing is logged.
 * Look at example/ShippingQuoteService for the pattern.
 */
@Service
public class PaymentService {
    // TODO: declare one SLF4J logger for this class (private static final, named after PaymentService).

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final PaymentGateway gateway;
    private final BigDecimal maxAmount;

    public PaymentService(PaymentGateway gateway, @Value("${payments.max-amount}") BigDecimal maxAmount) {
        this.gateway = gateway;
        this.maxAmount = maxAmount;
    }

    public PaymentResponse pay(PaymentRequest request) {
        UUID id = UUID.randomUUID();
        String maskedCard = SensitiveData.maskCardNumber(request.cardNumber());



        // TODO (DEBUG): "Payment received" with paymentId, the masked email, the masked card,
        // amount and currency. Use {} placeholders. Never log request.cardNumber() or
        // request.customerEmail() directly.

        String maskedEmail = SensitiveData.maskEmail(request.customerEmail());
        log.debug("Payment received: paymentId={}, email={}, card={}, amount={}, currency={}",
                id ,
                maskedEmail ,
                maskedCard ,
                request.amount(),
                request.currency());

        if (request.amount().compareTo(maxAmount) > 0) {
            // TODO (WARN): "Payment declined" with paymentId, reason=LIMIT_EXCEEDED, amount and limit.
            log.warn("Payment declined: paymentId={} ,  reason={} , amount={}, limit={}",
                    id ,
                    "LIMIT_EXCEEDED" ,
                    request.amount(),
                    maxAmount);
            return new PaymentResponse(id, PaymentStatus.DECLINED, request.amount(),
                    request.currency(), maskedCard, "LIMIT_EXCEEDED");
        }

        // If the gateway throws, do not catch or log it here. ApiExceptionHandler logs it once.
        gateway.charge(id, request.cardNumber());

        // TODO (INFO): "Payment approved" with paymentId, amount and currency.
        log.info("Payment approved: paymentId={},amount={}, currency={}" ,
                id ,
                request.amount(),
                request.currency());
        return new PaymentResponse(id, PaymentStatus.APPROVED, request.amount(),
                request.currency(), maskedCard, null);
    }
}
