package com.example.loggingbasics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.loggingbasics.payment.PaymentService;
import com.example.loggingbasics.web.CorrelationIdFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Task 3: one id per request, in MDC and in the response header. */
@SpringBootTest
@AutoConfigureMockMvc
class CorrelationIdTest {
    private static final String BODY = """
            {"customerEmail":"anna.petrosyan@example.com","cardNumber":"4111111111111111",
             "amount":250.00,"currency":"AMD"}""";

    @Autowired MockMvc mockMvc;

    @Test
    void clientCorrelationIdIsReusedInLogsAndResponse() throws Exception {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            MvcResult result = mockMvc.perform(post("/api/payments")
                            .header(CorrelationIdFilter.HEADER, "order-42")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals("order-42", result.getResponse().getHeader(CorrelationIdFilter.HEADER));
            List<ILoggingEvent> info = logs.at(Level.INFO);
            assertFalse(info.isEmpty(), "PaymentService should log an INFO line");
            assertEquals("order-42", info.get(0).getMDCPropertyMap().get(CorrelationIdFilter.MDC_KEY));
        }
    }

    @Test
    void missingCorrelationIdIsGenerated() throws Exception {
        try (LogCapture logs = LogCapture.of(PaymentService.class)) {
            MvcResult result = mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isOk())
                    .andReturn();

            String header = result.getResponse().getHeader(CorrelationIdFilter.HEADER);
            assertNotNull(header, "the response must contain " + CorrelationIdFilter.HEADER);
            assertDoesNotThrow(() -> UUID.fromString(header), "a generated id should be a UUID");
            assertFalse(logs.at(Level.INFO).isEmpty(), "PaymentService should log an INFO line");
            assertEquals(header, logs.at(Level.INFO).get(0).getMDCPropertyMap().get(CorrelationIdFilter.MDC_KEY));
        }
    }

    @Test
    void unsafeCorrelationIdIsReplaced() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/payments")
                        .header(CorrelationIdFilter.HEADER, "abc\nFAKE LOG LINE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk())
                .andReturn();

        String header = result.getResponse().getHeader(CorrelationIdFilter.HEADER);
        assertNotNull(header);
        assertNotEquals("abc\nFAKE LOG LINE", header);
    }

    @Test
    void mdcIsClearedAfterTheRequest() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header(CorrelationIdFilter.HEADER, "order-43")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY),
                "remove the MDC value in finally, or the next request on this thread gets the wrong id");
    }
}
