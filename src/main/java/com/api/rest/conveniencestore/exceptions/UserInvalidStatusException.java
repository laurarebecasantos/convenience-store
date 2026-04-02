package com.api.rest.conveniencestore.exceptions;

import com.api.rest.conveniencestore.model.User;

public class UserInvalidStatusException extends RuntimeException {
    public UserInvalidStatusException(String message) {
        super(message);
    }
}

