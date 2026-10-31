package com.example.productvalidation.dto;

import com.example.productvalidation.entity.Product;

import java.math.BigDecimal;

/** HTTP output. The database entity is never returned directly. */
public record ProductResponse(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        String category
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getSku(),
                product.getPrice(), product.getCategory());
    }
}
