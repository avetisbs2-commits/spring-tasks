package com.example.productvalidation.service;

import com.example.productvalidation.dto.ProductRequest;
import com.example.productvalidation.dto.ProductResponse;
import com.example.productvalidation.entity.Product;
import com.example.productvalidation.exception.DuplicateSkuException;
import com.example.productvalidation.exception.InvalidFilterException;
import com.example.productvalidation.exception.ProductNotFoundException;
import com.example.productvalidation.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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


        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0 ||
                maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0 ||
                minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidFilterException("Invalid price filters");
        }

        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(product ->
                        category == null ||
                                product.getCategory().equalsIgnoreCase(category))
                .filter(product ->
                        minPrice == null ||
                                product.getPrice().compareTo(minPrice) >= 0)
                .filter(product ->
                        maxPrice == null ||
                                product.getPrice().compareTo(maxPrice) <= 0)
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getPrice(),
                        product.getCategory()
                ))
                .toList();
    }

    public ProductResponse get(Long id) {
        // TODO: find by id or throw ProductNotFoundException; map to ProductResponse.
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductResponse(product.getId(), product.getName(), product.getSku(),
                product.getPrice(), product.getCategory());

    }

    public ProductResponse update(Long id, ProductRequest request) {
        // TODO: find by id; reject another product with the same SKU; change its fields;
        // save and map it. Never use an id supplied in the JSON body.
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        for (Product productExisted : productRepository.findAll()) {
            if (!productExisted.getId().equals(product.getId()) &&
                    productExisted.getSku().equalsIgnoreCase(request.sku())) {
                throw new DuplicateSkuException(request.sku());
            }
        }

        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setCategory(request.category());

        productRepository.save(product);

        return new ProductResponse(product.getId(), product.getName(), product.getSku(),
                product.getPrice(), product.getCategory());
    }

    public void delete(Long id) {
        // TODO: return 404 for a missing product, otherwise delete it.
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
       productRepository.delete(product);
    }
}
