package com.example.jobbasics.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** COMPLETE EXAMPLE: read the state produced by the recurring job. */
@RestController
@RequestMapping("/api/examples/heartbeat")
public class HeartbeatController {
    private final HeartbeatJob heartbeatJob;

    public HeartbeatController(HeartbeatJob heartbeatJob) {
        this.heartbeatJob = heartbeatJob;
    }

    @GetMapping
    public HeartbeatSnapshot getHeartbeat() {
        return heartbeatJob.snapshot();
    }
}
