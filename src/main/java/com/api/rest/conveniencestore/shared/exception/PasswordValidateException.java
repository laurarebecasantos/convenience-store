package com.api.rest.conveniencestore.shared.exception;

public class PasswordValidateException extends RuntimeException {
    public PasswordValidateException(String message) {
        super(message);
    }
}