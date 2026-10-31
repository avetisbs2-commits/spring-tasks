package com.example.jobbasics.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/** COMPLETE EXAMPLE: Spring calls tick repeatedly without an HTTP request. */
@Component
public class HeartbeatJob {
    private final Clock clock;
    private final AtomicInteger runs = new AtomicInteger();
    private volatile Instant lastRunAt;

    public HeartbeatJob(Clock clock) {
        this.clock = clock;
    }

    @Scheduled(initialDelayString = "${jobs.heartbeat.initial-delay-ms}",
               fixedDelayString = "${jobs.heartbeat.delay-ms}")
    public void tick() {
        lastRunAt = clock.instant();
        runs.incrementAndGet();
    }

    public HeartbeatSnapshot snapshot() {
        return new HeartbeatSnapshot(runs.get(), lastRunAt);
    }
}
