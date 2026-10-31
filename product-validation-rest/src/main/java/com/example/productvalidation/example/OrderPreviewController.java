package com.example.productvalidation.example;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** COMPLETE EXAMPLE: a preview returns 200 because it creates no stored resource. */
@RestController
@RequestMapping("/api/examples/order-previews")
public class OrderPreviewController {
    private final OrderPreviewService service;

    public OrderPreviewController(OrderPreviewService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderPreviewResponse> preview(@Valid @RequestBody OrderPreviewRequest request) {
        return ResponseEntity.ok(service.preview(request));
    }
}
