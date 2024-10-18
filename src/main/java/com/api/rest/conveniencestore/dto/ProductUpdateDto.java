package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.Status;

import java.time.LocalDate;

public record ProductUpdateDto(

        Double price,

        Integer stockQuantity,

        LocalDate expirationDate,

        Status status) {
}

