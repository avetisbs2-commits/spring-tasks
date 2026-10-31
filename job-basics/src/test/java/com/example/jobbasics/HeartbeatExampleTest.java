package com.example.jobbasics;

import com.example.jobbasics.example.HeartbeatJob;
import com.example.jobbasics.example.HeartbeatSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The complete recurring example is green before students edit anything. */
class HeartbeatExampleTest {
    @Test
    void tickRecordsEachRun() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        HeartbeatJob job = new HeartbeatJob(Clock.fixed(now, ZoneOffset.UTC));
        HeartbeatSnapshot before = job.snapshot();
        assertEquals(0, before.runs());
        assertNull(before.lastRunAt());

        job.tick();
        job.tick();
        assertEquals(2, job.snapshot().runs());
        assertEquals(now, job.snapshot().lastRunAt());
    }
}
