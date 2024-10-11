package com.api.rest.conveniencestore.exceptions;

public class ProductInsufficientStockException extends Exception {
    public ProductInsufficientStockException (String message) {
        super(message);
    }
}
