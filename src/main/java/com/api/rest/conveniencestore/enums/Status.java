package com.api.rest.conveniencestore.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    ACTIVE,
    INACTIVE,
    REGISTERED,
    APPROVED,
    CANCELLED;

    @JsonCreator
    public static Status fromValueStatus(String value) { //converte uma string que vem do json para uma enum
        return Status.valueOf(value.toUpperCase());
    }


}
