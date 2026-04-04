package com.api.rest.conveniencestore.shared.exception;

public class SaleNotValidPaymentMethodException extends RuntimeException {
    public SaleNotValidPaymentMethodException(String message) {
        super(message);
    }
}

