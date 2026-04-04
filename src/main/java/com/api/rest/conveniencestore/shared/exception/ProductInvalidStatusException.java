package com.api.rest.conveniencestore.shared.exception;

public class ProductInvalidStatusException extends RuntimeException{
    public ProductInvalidStatusException(String message) {
        super(message);
    }
}

