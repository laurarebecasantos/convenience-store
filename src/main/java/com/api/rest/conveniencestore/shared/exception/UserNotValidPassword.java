package com.api.rest.conveniencestore.shared.exception;

public class UserNotValidPassword extends RuntimeException {
    public UserNotValidPassword(String message) {
        super(message);
    }
}

