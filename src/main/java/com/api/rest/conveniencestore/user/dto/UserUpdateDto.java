package com.api.rest.conveniencestore.user.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        String username,

        String password,

        @Email(message = "Email must be valid")
        String email) {
}