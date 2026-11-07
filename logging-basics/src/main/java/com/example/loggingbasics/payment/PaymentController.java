package com.example.loggingbasics.payment;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** COMPLETE: the controller has no logging; requests are logged where the work happens. */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return service.pay(request);
    }
}
