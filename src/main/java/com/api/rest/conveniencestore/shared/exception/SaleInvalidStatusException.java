package com.api.rest.conveniencestore.shared.exception;

public class SaleInvalidStatusException extends RuntimeException{
    public SaleInvalidStatusException(String message) {
        super (message);
    }
}

