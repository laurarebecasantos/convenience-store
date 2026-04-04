package com.api.rest.conveniencestore.shared.exception;

import com.api.rest.conveniencestore.user.model.User;

public class UserInvalidStatusException extends RuntimeException {
    public UserInvalidStatusException(String message) {
        super(message);
    }
}

