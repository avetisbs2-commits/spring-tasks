package com.example.productvalidation;

import com.example.productvalidation.dto.ProductRequest;
import com.example.productvalidation.dto.ProductResponse;
import com.example.productvalidation.exception.DuplicateSkuException;
import com.example.productvalidation.repository.ProductRepository;
import com.example.productvalidation.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The complete create service method is a reference for the remaining methods. */
@SpringBootTest
@Transactional
class ProductServiceTest {
    @Autowired ProductService service;
    @Autowired ProductRepository repository;

    @Test
    void createPersistsAndMapsToResponse() {
        ProductResponse created = service.create(new ProductRequest("Notebook", "BOOK-101",
                new BigDecimal("12.50"), "Stationery"));
        assertEquals("Notebook", created.name());
        assertEquals("BOOK-101", repository.findById(created.id()).orElseThrow().getSku());
    }

    @Test
    void duplicateSkuIsRejectedBeforeAnotherRowIsSaved() {
        service.create(new ProductRequest("Notebook", "BOOK-101", new BigDecimal("12.50"), "Stationery"));
        assertThrows(DuplicateSkuException.class, () -> service.create(new ProductRequest(
                "Another", "book-101", new BigDecimal("13.00"), "Stationery")));
        assertEquals(1, repository.count());
    }
}
