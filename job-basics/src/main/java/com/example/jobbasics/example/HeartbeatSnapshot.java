package com.example.jobbasics.example;

import java.time.Instant;

public record HeartbeatSnapshot(int runs, Instant lastRunAt) {}
