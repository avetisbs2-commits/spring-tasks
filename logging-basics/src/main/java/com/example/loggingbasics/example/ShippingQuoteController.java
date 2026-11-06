package com.example.loggingbasics.example;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/** COMPLETE EXAMPLE: GET /api/examples/shipping-quote?weightKg=3 */
@Validated
@RestController
@RequestMapping("/api/examples/shipping-quote")
public class ShippingQuoteController {
    private final ShippingQuoteService service;

    public ShippingQuoteController(ShippingQuoteService service) {
        this.service = service;
    }

    @GetMapping
    public ShippingQuote quote(@RequestParam
                               @DecimalMin(value = "0.0", inclusive = false)
                               @DecimalMax("100") BigDecimal weightKg) {
        return service.quote(weightKg);
    }
}
