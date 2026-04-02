package com.api.rest.conveniencestore.exceptions;

public class ProductInsufficientStockException extends RuntimeException {
    public ProductInsufficientStockException (String message) {
        super(message);
    }
}

