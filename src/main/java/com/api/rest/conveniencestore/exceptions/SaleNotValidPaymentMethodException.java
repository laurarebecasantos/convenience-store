package com.api.rest.conveniencestore.exceptions;

public class SaleNotValidPaymentMethodException extends RuntimeException {
    public SaleNotValidPaymentMethodException(String message) {
        super(message);
    }
}

