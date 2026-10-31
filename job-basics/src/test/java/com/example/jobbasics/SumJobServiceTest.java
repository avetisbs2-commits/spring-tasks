package com.example.jobbasics;

import com.example.jobbasics.job.CreateSumJobRequest;
import com.example.jobbasics.job.JobNotFoundException;
import com.example.jobbasics.job.JobSnapshot;
import com.example.jobbasics.job.JobStatus;
import com.example.jobbasics.job.SumJobService;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Captures the scheduled Runnable so tests never sleep. */
class SumJobServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private static class CapturingScheduler extends ThreadPoolTaskScheduler {
        Runnable task;
        Instant scheduledFor;
        int calls;

        @Override
        public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
            this.task = task;
            this.scheduledFor = startTime;
            this.calls++;
            return null;
        }
    }

    @Test
    void submittingSchedulesOneRunAndReturnsBeforeWorkFinishes() {
        CapturingScheduler scheduler = new CapturingScheduler();
        SumJobService service = new SumJobService(scheduler, Clock.fixed(NOW, ZoneOffset.UTC));

        JobSnapshot submitted = service.submit(new CreateSumJobRequest(10, 5));

        assertNotNull(submitted.id());
        assertEquals(JobStatus.SCHEDULED, submitted.status());
        assertNull(submitted.result());
        assertEquals(NOW.plusSeconds(5), submitted.runAt());
        assertEquals(submitted.runAt(), scheduler.scheduledFor);
        assertEquals(1, scheduler.calls);

        scheduler.task.run();
        JobSnapshot finished = service.get(submitted.id());
        assertEquals(JobStatus.COMPLETED, finished.status());
        assertEquals(55L, finished.result());
        assertNull(finished.error());
    }

    @Test
    void unknownJobIsNotFound() {
        SumJobService service = new SumJobService(new CapturingScheduler(), Clock.fixed(NOW, ZoneOffset.UTC));
        assertThrows(JobNotFoundException.class, () -> service.get(UUID.randomUUID()));
    }
}
