package com.example.loggingbasics.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * COMPLETE EXAMPLE: read this class before the student tasks.
 *
 * <ul>
 *   <li>One logger per class, named after the class.</li>
 *   <li>{@code {}} placeholders instead of string concatenation.</li>
 *   <li>DEBUG for details, INFO for a finished business action, WARN for an unusual but handled case.</li>
 * </ul>
 */
@Service
public class ShippingQuoteService {
    private static final Logger log = LoggerFactory.getLogger(ShippingQuoteService.class);

    private static final BigDecimal PRICE_PER_KG = new BigDecimal("2.50");
    private static final BigDecimal HEAVY_LIMIT_KG = new BigDecimal("30");
    private static final BigDecimal HEAVY_SURCHARGE = new BigDecimal("15.00");

    public ShippingQuote quote(BigDecimal weightKg) {
        log.debug("Calculating shipping quote: weightKg={}", weightKg);

        BigDecimal price = weightKg.multiply(PRICE_PER_KG);
        boolean heavy = weightKg.compareTo(HEAVY_LIMIT_KG) > 0;
        if (heavy) {
            price = price.add(HEAVY_SURCHARGE);
            log.warn("Heavy parcel surcharge applied: weightKg={}, limitKg={}", weightKg, HEAVY_LIMIT_KG);
        }
        price = price.setScale(2, RoundingMode.HALF_UP);

        log.info("Shipping quote created: weightKg={}, price={}", weightKg, price);
        return new ShippingQuote(weightKg, price, heavy);
    }
}
