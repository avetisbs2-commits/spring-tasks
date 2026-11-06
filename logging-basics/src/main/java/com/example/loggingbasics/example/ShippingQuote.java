package com.example.loggingbasics.example;

import java.math.BigDecimal;

public record ShippingQuote(BigDecimal weightKg, BigDecimal price, boolean heavy) {}
