package com.api.rest.conveniencestore.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CASH,
    CREDIT,
    DEBIT;

    @JsonCreator
    public static PaymentMethod fromValuePaymentMethod (String value) {
        return PaymentMethod.valueOf(value.toUpperCase());
    }
}


