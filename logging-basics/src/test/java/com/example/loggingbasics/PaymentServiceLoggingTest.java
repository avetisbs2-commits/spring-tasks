package com.example.loggingbasics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.loggingbasics.payment.PaymentGateway;
import com.example.loggingbasics.payment.PaymentRequest;
import com.example.loggingbasics.payment.PaymentResponse;
import com.example.loggingbasics.payment.PaymentService;
import com.example.loggingbasics.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Task 2: the right level, a parameterized message, and no secrets. */
class PaymentServiceLoggingTest {
    private static final String CARD = "4111111111111111";
    private static final String EMAIL = "anna.petrosyan@example.com";

    private final PaymentService service = new PaymentService(new PaymentGateway(), new BigDecimal("1000"));

    @Test
    void approvedPaymentLogsOneInfoWithPaymentId() {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            PaymentResponse response = service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("250.00"), "AMD"));

            assertEquals(PaymentStatus.APPROVED, response.status());
            List<ILoggingEvent> info = logs.at(Level.INFO);
            assertEquals(1, info.size(), "log exactly one INFO line when a payment is approved");

            ILoggingEvent event = info.get(0);
            assertParameterized(event);
            assertTrue(event.getFormattedMessage().contains(response.id().toString()),
                    "the INFO line must contain the paymentId");
            assertTrue(event.getFormattedMessage().contains("250.00"), "the INFO line must contain the amount");
            assertTrue(logs.at(Level.WARN).isEmpty(), "an approved payment is not a warning");
        }
    }

    @Test
    void receivedPaymentLogsDebugDetailsWithMaskedData() {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("250.00"), "AMD"));

            List<ILoggingEvent> debug = logs.at(Level.DEBUG);
            assertFalse(debug.isEmpty(), "log the incoming payment details at DEBUG");
            debug.forEach(PaymentServiceLoggingTest::assertParameterized);

            String debugText = debug.get(0).getFormattedMessage();
            assertTrue(debugText.contains("************1111"), "log the masked card number");
            assertTrue(debugText.contains("a***@example.com"), "log the masked email");
        }
    }

    @Test
    void declinedPaymentLogsWarnWithReason() {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            PaymentResponse response = service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("5000"), "AMD"));

            assertEquals(PaymentStatus.DECLINED, response.status());
            List<ILoggingEvent> warn = logs.at(Level.WARN);
            assertEquals(1, warn.size(), "log exactly one WARN line when a payment is declined");
            assertParameterized(warn.get(0));
            assertTrue(warn.get(0).getFormattedMessage().contains("LIMIT_EXCEEDED"), "the WARN line must say why");
            assertTrue(warn.get(0).getFormattedMessage().contains(response.id().toString()));
            assertTrue(logs.at(Level.INFO).isEmpty(), "a declined payment is not logged as approved");
        }
    }

    @Test
    void noLogLineContainsTheFullCardNumberOrEmail() {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("250.00"), "AMD"));
            service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("5000"), "AMD"));

            assertFalse(logs.events().isEmpty(), "PaymentService should write logs");
            String all = logs.allText();
            assertFalse(all.contains(CARD), "the full card number leaked into the logs");
            assertFalse(all.contains(EMAIL), "the full email leaked into the logs");
        }
    }

    @Test
    void loggerIsNamedAfterTheClass() {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            service.pay(new PaymentRequest(EMAIL, CARD, new BigDecimal("250.00"), "AMD"));

            assertFalse(logs.events().isEmpty(), "PaymentService should write logs");
            logs.events().forEach(e -> assertEquals(PaymentService.class.getName(), e.getLoggerName()));
        }
    }

    /** "Payment approved: paymentId={}" with arguments, not "Payment approved: " + id. */
    static void assertParameterized(ILoggingEvent event) {
        assertTrue(event.getMessage().contains("{}"),
                "use {} placeholders, not string concatenation: " + event.getMessage());
        assertNotNull(event.getArgumentArray(), "pass values as arguments: " + event.getMessage());
    }
}
