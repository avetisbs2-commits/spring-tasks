package com.example.loggingbasics.logsafe;

/**
 * STUDENT TASK 1: turn personal or secret values into safe text for logs and responses.
 * Right now both methods return the real value, so the card number and email leak into the logs.
 */
public final class SensitiveData {
    private SensitiveData() {
    }

    /** "4111111111111111" -> "************1111" */
    public static String maskCardNumber(String cardNumber) {
        // TODO: keep only the last 4 digits and replace every other character with '*'.
        // null or shorter than 4 characters -> "****".
        return cardNumber;
    }

    /** "anna.petrosyan@example.com" -> "a***@example.com" */
    public static String maskEmail(String email) {
        // TODO: keep the first character and everything from '@'; put "***" in between.
        // null, no '@', or '@' as the first character -> "***".
        return email;
    }
}
