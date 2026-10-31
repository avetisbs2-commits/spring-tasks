package com.example.productvalidation.exception;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("SKU " + sku + " already exists");
    }
}
