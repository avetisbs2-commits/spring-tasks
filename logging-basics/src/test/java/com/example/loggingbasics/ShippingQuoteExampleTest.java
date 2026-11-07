package com.example.loggingbasics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.example.loggingbasics.example.ShippingQuoteService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The complete example should be green before any student TODO is done. */
class ShippingQuoteExampleTest {
    private final ShippingQuoteService service = new ShippingQuoteService();

    @Test
    void normalParcelLogsDebugAndInfo() {
        try (LogCapture logs = LogCapture.of(ShippingQuoteService.class)) {
            service.quote(new BigDecimal("3"));

            assertEquals(1, logs.at(Level.DEBUG).size());
            assertEquals(0, logs.at(Level.WARN).size());
            List<ILoggingEvent> info = logs.at(Level.INFO);
            assertEquals(1, info.size());

            ILoggingEvent event = info.get(0);
            assertEquals("Shipping quote created: weightKg={}, price={}", event.getMessage());
            assertArrayEquals(new Object[]{new BigDecimal("3"), new BigDecimal("7.50")}, event.getArgumentArray());
            assertEquals("Shipping quote created: weightKg=3, price=7.50", event.getFormattedMessage());
        }
    }

    @Test
    void heavyParcelAlsoLogsWarn() {
        try (LogCapture logs = LogCapture.of(ShippingQuoteService.class)) {
            service.quote(new BigDecimal("40"));

            List<ILoggingEvent> warn = logs.at(Level.WARN);
            assertEquals(1, warn.size());
            assertTrue(warn.get(0).getFormattedMessage().contains("weightKg=40"));
        }
    }
}
