package com.example.productvalidation;

import com.example.productvalidation.dto.ProductRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Complete ProductRequest validation is a reference for the remaining exercises. */
class ProductRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validRequestHasNoViolations() {
        ProductRequest request = new ProductRequest("Notebook", "BOOK-101", new BigDecimal("12.50"), "Stationery");
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void badFieldsAreReportedTogether() {
        ProductRequest request = new ProductRequest(" ", "bad sku", BigDecimal.ZERO, "");
        Set<String> fields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertEquals(Set.of("name", "sku", "price", "category"), fields);
    }

    @Test
    void missingPriceAndLongNameAreInvalid() {
        ProductRequest request = new ProductRequest("N".repeat(81), "BOOK-101", null, "Stationery");
        Set<String> fields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertEquals(Set.of("name", "price"), fields);
    }
}
