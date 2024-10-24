package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.PaymentMethod;
import com.api.rest.conveniencestore.model.Sale;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SaleListingDto(
        Long id,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dateSale,

        double totalValue,

        PaymentMethod paymentMethod,

        int quantity,

        String description
) {
    public SaleListingDto(Sale sale) {
        this(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalValue(),
                sale.getPaymentMethod(),
                sale.getQuantity(),
                sale.getDescription()
        );
    }
}
