package com.example.productvalidation.example;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** COMPLETE EXAMPLE: annotations describe the accepted HTTP input. */
public record OrderPreviewRequest(
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal unitPrice
) {}
