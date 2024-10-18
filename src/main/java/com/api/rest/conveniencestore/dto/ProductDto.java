package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProductDto(


        @Column(nullable = false, unique = true)
        @NotBlank(message = "Product name cannot be blank")
        String name,

        Category category,

        @NotNull(message = "Price cannot be null")
        double price,

        @Column(nullable = false, name = "stock_quantity")
        @NotNull(message = "Stock quantity cannot be null")
        int stockQuantity,

        LocalDate expirationDate) {
}

