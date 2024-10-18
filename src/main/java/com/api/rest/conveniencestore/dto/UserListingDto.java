package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.User;

public record UserListingDto(
        Long id,

        String username,

        String email,

        Status status,

        Roles role) {

    public UserListingDto(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus(),
                user.getRole()
        );
    }
}
