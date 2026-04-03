package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.model.Client;

public record ClientListingDto(Long id, String name, String cpf, int pointsBalance) {

    public ClientListingDto(Client client) {
        this(client.getId(), client.getName(), client.getCpf(), client.getPointsBalance());
    }
}
