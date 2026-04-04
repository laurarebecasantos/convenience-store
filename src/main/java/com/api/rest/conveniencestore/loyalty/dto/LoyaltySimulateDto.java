package com.api.rest.conveniencestore.loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LoyaltySimulateDto(
        @NotNull Long clientId,
        @NotNull @Min(0) Double purchaseAmount,
        @NotNull @Min(0) Integer pointsToUse
) {}
