package com.example.jobbasics.job;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Provided input: wait 1–30 seconds, then sum integers from 1 to upperBound. */
public record CreateSumJobRequest(
        @NotNull @Min(1) @Max(1000) Integer upperBound,
        @NotNull @Min(1) @Max(30) Integer delaySeconds
) {}
