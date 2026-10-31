package com.example.productvalidation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** COMPLETE EXAMPLE: validate HTTP input before it reaches ProductService. */
public record ProductRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9-]{3,20}") String sku,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotBlank String category
) {}
