package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProductDto(

        @NotBlank(message = "Product name cannot be blank")
        String name,

        Category category,

        @NotNull(message = "Price cannot be null")
        double price,

        @NotNull(message = "Stock quantity cannot be null")
        int stockQuantity,

        LocalDate expirationDate) {
}