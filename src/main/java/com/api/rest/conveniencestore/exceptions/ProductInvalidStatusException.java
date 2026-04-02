package com.api.rest.conveniencestore.exceptions;

public class ProductInvalidStatusException extends RuntimeException{
    public ProductInvalidStatusException(String message) {
        super(message);
    }
}

