package com.example.jobbasics.job;

import java.time.Instant;
import java.util.UUID;

/** Immutable state. Replacing the map value makes changes visible across threads. */
public record JobSnapshot(
        UUID id,
        int upperBound,
        Instant runAt,
        JobStatus status,
        Long result,
        String error
) {}
