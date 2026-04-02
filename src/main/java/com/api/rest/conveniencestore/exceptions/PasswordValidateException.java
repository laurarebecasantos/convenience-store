package com.api.rest.conveniencestore.exceptions;

public class PasswordValidateException extends RuntimeException {
    public PasswordValidateException(String message) {
        super(message);
    }
}