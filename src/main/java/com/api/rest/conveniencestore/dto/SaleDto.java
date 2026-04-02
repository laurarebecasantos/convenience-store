package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleDto(

        @NotEmpty(message = "Product IDs cannot be empty")
        List<Long> productIds,

        @NotEmpty(message = "Quantities cannot be empty")
        List<Integer> quantity,

        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod,

        @NotBlank(message = "Client CPF cannot be blank")
        String clientCpf
) {
}