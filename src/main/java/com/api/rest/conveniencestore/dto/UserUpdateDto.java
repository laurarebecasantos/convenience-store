package com.api.rest.conveniencestore.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$", message = "Password must be at least 8 characters long and include both letters and numbers")
        String password,

        @Email(message = "Email must be valid")
        String email) {
}



