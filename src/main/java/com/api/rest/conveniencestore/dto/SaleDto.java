package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleDto(

        @Column(name = "id")
        @NotEmpty(message = "Product IDs cannot be empty")
        List<Long> productIds,

        @NotEmpty(message = "Quantities cannot be empty")
        List<Integer> quantity,

        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod
) {

}

