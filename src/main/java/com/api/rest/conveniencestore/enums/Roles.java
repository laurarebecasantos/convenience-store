package com.api.rest.conveniencestore.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Roles {
    USER,
    ADMIN;

    @JsonCreator
    public static Roles fromValueRoles(String value) { //converte uma string que vem do json para uma enum
        return Roles.valueOf(value.toUpperCase());
    }
}
