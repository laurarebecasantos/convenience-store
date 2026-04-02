package com.api.rest.conveniencestore.exceptions;

public class UserNotValidPassword extends RuntimeException {
    public UserNotValidPassword(String message) {
        super(message);
    }
}

