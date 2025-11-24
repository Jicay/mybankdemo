package com.example.mybank.infrastructure.driving.rest.dto;

import com.example.mybank.domain.model.Client;

public record ClientDTO(
        String id,
        String lastName,
        String firstName
) {
    public static ClientDTO fromDomain(Client client) {
        return new ClientDTO(
                client.id().value().toString(),
                client.lastName().value(),
                client.firstName().value()
        );
    }
}