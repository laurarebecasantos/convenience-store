package com.api.rest.conveniencestore.user.dto;

import com.api.rest.conveniencestore.shared.enums.Roles;
import com.api.rest.conveniencestore.shared.enums.Status;
import com.api.rest.conveniencestore.user.model.User;

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