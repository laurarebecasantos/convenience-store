package com.api.rest.conveniencestore.product.dto;

import com.api.rest.conveniencestore.shared.enums.Status;

import java.time.LocalDate;

public record ProductUpdateDto(

        Double price,

        Integer stockQuantity,

        LocalDate expirationDate,

        Status status) {
}