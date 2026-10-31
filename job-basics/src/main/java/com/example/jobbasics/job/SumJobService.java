package com.example.jobbasics.job;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** STUDENT TASK: schedule one execution and track its state. */
@Service
public class SumJobService {
    private final TaskScheduler scheduler;
    private final Clock clock;
    private final Map<UUID, JobSnapshot> jobs = new ConcurrentHashMap<>();

    public SumJobService(TaskScheduler scheduler, Clock clock) {
        this.scheduler = scheduler;
        this.clock = clock;
    }

    public JobSnapshot submit(CreateSumJobRequest request) {
        // TODO:
        // 1. Create a UUID and runAt = clock.instant().plusSeconds(request.delaySeconds()).
        // 2. Store a JobSnapshot with status SCHEDULED and null result/error.
        // 3. Call scheduler.schedule(() -> run(id), runAt) exactly once.
        // 4. Return the SCHEDULED snapshot immediately; do not calculate here.
        throw new UnsupportedOperationException("Implement submit");
    }

    public JobSnapshot get(UUID id) {
        JobSnapshot snapshot = jobs.get(id);
        if (snapshot == null) {
            throw new JobNotFoundException(id);
        }
        return snapshot;
    }

    private void run(UUID id) {
        // TODO: on the scheduler thread, change status to RUNNING, calculate
        // 1 + ... + upperBound, then set COMPLETED with the result.
        // If calculation fails, set FAILED with an error message.
        // Tip: use jobs.computeIfPresent(id, (key, old) -> new JobSnapshot(...))
        // to replace each immutable snapshot safely.
    }
}
