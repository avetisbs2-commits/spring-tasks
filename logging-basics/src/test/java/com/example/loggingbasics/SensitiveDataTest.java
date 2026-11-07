package com.example.loggingbasics;

import com.example.loggingbasics.logsafe.SensitiveData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Task 1: mask secrets before they reach a log line. */
class SensitiveDataTest {
    @Test
    void cardNumberKeepsOnlyLastFourDigits() {
        assertEquals("************1111", SensitiveData.maskCardNumber("4111111111111111"));
    }

    @Test
    void shortOrMissingCardNumberIsFullyHidden() {
        assertEquals("****", SensitiveData.maskCardNumber("123"));
        assertEquals("****", SensitiveData.maskCardNumber(null));
    }

    @Test
    void emailKeepsFirstLetterAndDomain() {
        assertEquals("a***@example.com", SensitiveData.maskEmail("anna.petrosyan@example.com"));
    }

    @Test
    void invalidOrMissingEmailIsFullyHidden() {
        assertEquals("***", SensitiveData.maskEmail("not-an-email"));
        assertEquals("***", SensitiveData.maskEmail("@example.com"));
        assertEquals("***", SensitiveData.maskEmail(null));
    }
}
