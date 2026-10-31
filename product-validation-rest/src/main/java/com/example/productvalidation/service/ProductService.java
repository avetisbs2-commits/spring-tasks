package com.example.productvalidation.service;

import com.example.productvalidation.dto.ProductRequest;
import com.example.productvalidation.dto.ProductResponse;
import com.example.productvalidation.entity.Product;
import com.example.productvalidation.exception.DuplicateSkuException;
import com.example.productvalidation.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/** Product rules and entity/DTO mapping live here, not in the controller. */
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        productRepository.findBySkuIgnoreCase(request.sku()).ifPresent(existing -> {
            throw new DuplicateSkuException(request.sku());
        });

        Product product = new Product(request.name(), request.sku(), request.price(), request.category());
        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> list(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        // TODO: reject negative bounds or minPrice > maxPrice with InvalidFilterException.
        // Return every product that matches all supplied filters; null means no filter.
        // Category comparison is case-insensitive. Include products at both price bounds.
        // For this small exercise, filtering findAll() in memory is sufficient.
        throw new UnsupportedOperationException("Implement list");
    }

    public ProductResponse get(Long id) {
        // TODO: find by id or throw ProductNotFoundException; map to ProductResponse.
        throw new UnsupportedOperationException("Implement get");
    }

    public ProductResponse update(Long id, ProductRequest request) {
        // TODO: find by id; reject another product with the same SKU; change its fields;
        // save and map it. Never use an id supplied in the JSON body.
        throw new UnsupportedOperationException("Implement update");
    }

    public void delete(Long id) {
        // TODO: return 404 for a missing product, otherwise delete it.
        throw new UnsupportedOperationException("Implement delete");
    }
}
