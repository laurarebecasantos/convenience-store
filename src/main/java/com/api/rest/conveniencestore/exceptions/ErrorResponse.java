package com.api.rest.conveniencestore.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        int status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    public ErrorResponse(String error, String message, int status) {
        this(error, message, status, LocalDateTime.now());
    }
}
