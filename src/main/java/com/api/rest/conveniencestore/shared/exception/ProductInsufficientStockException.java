package com.api.rest.conveniencestore.shared.exception;

public class ProductInsufficientStockException extends RuntimeException {
    public ProductInsufficientStockException (String message) {
        super(message);
    }
}

