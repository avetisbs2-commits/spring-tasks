package com.example.jobbasics.job;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** GET is complete; students implement POST to schedule a one-time job. */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final SumJobService service;

    public JobController(SumJobService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JobSnapshot> submit(@Valid @RequestBody CreateSumJobRequest request) {
        // TODO: call service.submit; return 202 Accepted with the snapshot and
        // Location: /api/jobs/{id}. It is accepted, but not finished yet.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSnapshot> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
}
