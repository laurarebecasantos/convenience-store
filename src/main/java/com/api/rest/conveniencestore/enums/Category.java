package com.api.rest.conveniencestore.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
        FUEL,
        FOOD,
        BEVERAGE,
        CLEANING_PRODUCTS;

        @JsonCreator
        public static Category fromValueCategory (String value) {
            return Category.valueOf(value.toUpperCase());
        }
}

