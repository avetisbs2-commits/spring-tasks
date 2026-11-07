package com.example.loggingbasics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.loggingbasics.payment.PaymentService;
import com.example.loggingbasics.web.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Task 4: log a failure once, at ERROR, with its stack trace. */
@SpringBootTest
@AutoConfigureMockMvc
class GatewayFailureLoggingTest {
    @Autowired MockMvc mockMvc;

    @Test
    void providerOutageIsLoggedOnceAsErrorWithException() throws Exception {
        try (LogCapture handlerLogs = LogCapture.of(ApiExceptionHandler.class);
             LogCapture serviceLogs = LogCapture.of(PaymentService.class)) {
            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"customerEmail":"anna.petrosyan@example.com","cardNumber":"4111111111110000",
                                     "amount":250.00,"currency":"AMD"}"""))
                    .andExpect(status().isBadGateway());

            List<ILoggingEvent> errors = handlerLogs.at(Level.ERROR);
            assertEquals(1, errors.size(), "ApiExceptionHandler should log one ERROR line");
            assertNotNull(errors.get(0).getThrowableProxy(),
                    "pass the exception as the last argument, so the stack trace is printed");
            assertEquals("java.io.IOException",
                    errors.get(0).getThrowableProxy().getCause().getClassName(),
                    "the stack trace should include the cause");

            assertTrue(serviceLogs.at(Level.ERROR).isEmpty(),
                    "log the failure in one place only, not in both the service and the handler");
            assertFalse(handlerLogs.allText().contains("4111111111110000"));
            assertTrue(serviceLogs.at(Level.INFO).isEmpty(), "a failed payment is not logged as approved");
        }
    }
}
