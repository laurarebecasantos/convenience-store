package com.api.rest.conveniencestore.sale.dto;

import com.api.rest.conveniencestore.shared.enums.PaymentMethod;
import com.api.rest.conveniencestore.sale.model.Sale;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SaleListingDto(
        Long id,

        Long clientId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dateSale,

        double totalValue,

        double discount,

        PaymentMethod paymentMethod,

        int quantity,

        String seller,

        int pointsEarned,

        int pointsUsed,

        String description
) {
    public SaleListingDto(Sale sale) {
        this(
                sale.getId(),
                sale.getClient() != null ? sale.getClient().getId() : null,
                sale.getSaleDate(),
                sale.getTotalValue(),
                sale.getDiscount(),
                sale.getPaymentMethod(),
                sale.getQuantity(),
                sale.getSeller(),
                sale.getPointsEarned(),
                sale.getPointsUsed(),
                sale.getDescription()
        );
    }
}