package com.example.productvalidation.example;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/** COMPLETE EXAMPLE: calculation belongs in the service. */
@Service
public class OrderPreviewService {
    public OrderPreviewResponse preview(OrderPreviewRequest request) {
        BigDecimal total = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        return new OrderPreviewResponse(total);
    }
}
