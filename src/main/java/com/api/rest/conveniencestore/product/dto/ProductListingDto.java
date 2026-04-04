package com.api.rest.conveniencestore.product.dto;

import com.api.rest.conveniencestore.shared.enums.Category;
import com.api.rest.conveniencestore.product.model.Product;

public record ProductListingDto(
        Long id,
        String name,
        Category category,
        double price,
        int stockQuantity,
        String expirationDate) {

    public ProductListingDto(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getExpirationDate() != null ? product.getExpirationDate().toString() : "N/A");
    }
}