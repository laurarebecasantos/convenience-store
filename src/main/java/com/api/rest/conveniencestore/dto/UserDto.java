package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(

        String username,

        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,

        Roles role,

        Status status) {
}