package com.api.rest.conveniencestore.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    ACTIVE,
    INACTIVE,
    REGISTERED,
    APPROVED,
    CANCELLED;

    @JsonCreator
    public static Status fromValueStatus(String value) {
        return Status.valueOf(value.toUpperCase());
    }


}
