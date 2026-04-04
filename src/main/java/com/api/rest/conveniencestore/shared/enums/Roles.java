package com.api.rest.conveniencestore.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Roles {
    USER,
    ADMIN;

    @JsonCreator
    public static Roles fromValueRoles(String value) {
        return Roles.valueOf(value.toUpperCase());
    }
}

