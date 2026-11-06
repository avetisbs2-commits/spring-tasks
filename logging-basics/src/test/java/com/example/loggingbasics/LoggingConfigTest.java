package com.example.loggingbasics;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Task 5: configure levels, the console pattern, and JSON logs. */
class LoggingConfigTest {

    @Nested
    @SpringBootTest
    class DefaultProfile {
        @Autowired Environment environment;

        @Test
        void ourPackageLogsAtDebug() {
            Logger ours = LoggerFactory.getLogger("com.example.loggingbasics.payment.PaymentService");
            assertTrue(ours.isDebugEnabled(), "set logging.level.com.example.loggingbasics=DEBUG");
        }

        @Test
        void springItselfStaysQuieter() {
            Logger spring = LoggerFactory.getLogger("org.springframework.web");
            assertFalse(spring.isDebugEnabled(), "do not turn on DEBUG for all of Spring");
        }

        @Test
        void consoleLinesShowTheCorrelationId() {
            String pattern = environment.getProperty("logging.pattern.correlation");
            assertNotNull(pattern, "set logging.pattern.correlation");
            assertTrue(pattern.contains("%X{correlationId"), "read the correlationId from MDC with %X{...}");
        }
    }

    @Nested
    class JsonProfile {
        @Test
        void jsonProfileTurnsOnStructuredLogs() throws Exception {
            // Read the file directly: starting Spring with JSON logs here would switch every later test to JSON output.
            Properties json = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application-json.properties"));
            assertEquals("ecs", json.getProperty("logging.structured.format.console"),
                    "set logging.structured.format.console in application-json.properties");
        }
    }
}
