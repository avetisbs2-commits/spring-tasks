package com.example.productvalidation.example;

import java.math.BigDecimal;

/** COMPLETE EXAMPLE: the response shape is separate from the request. */
public record OrderPreviewResponse(BigDecimal total) {}
