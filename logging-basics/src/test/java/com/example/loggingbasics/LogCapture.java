package com.example.loggingbasics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Test helper: records every log event written by one class, so a test can
 * check the level, the message template, the arguments, the MDC and the exception.
 *
 * <pre>
 * try (LogCapture logs = LogCapture.of(PaymentService.class)) {
 *     service.pay(request);
 *     logs.events() ...
 * }
 * </pre>
 */
public final class LogCapture implements AutoCloseable {
    private final Logger logger;
    private final Level previousLevel;
    private final List<ILoggingEvent> events = new CopyOnWriteArrayList<>();
    private final AppenderBase<ILoggingEvent> appender = new AppenderBase<>() {
        @Override
        protected void append(ILoggingEvent event) {
            event.prepareForDeferredProcessing(); // copies MDC now, before the request ends
            events.add(event);
        }
    };

    private LogCapture(Class<?> type) {
        logger = (Logger) LoggerFactory.getLogger(type);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.DEBUG); // capture everything, whatever the app config says
        appender.start();
        logger.addAppender(appender);
    }

    public static LogCapture of(Class<?> type) {
        return new LogCapture(type);
    }

    public List<ILoggingEvent> events() {
        return List.copyOf(events);
    }

    public List<ILoggingEvent> at(Level level) {
        return events.stream().filter(e -> e.getLevel() == level).toList();
    }

    /** Every log line as it would be printed, joined together. */
    public String allText() {
        StringBuilder text = new StringBuilder();
        for (ILoggingEvent event : events) {
            text.append(event.getFormattedMessage()).append('\n');
            event.getMDCPropertyMap().values().forEach(v -> text.append(v).append('\n'));
        }
        return text.toString();
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        logger.setLevel(previousLevel);
    }
}
