package com.example.mybank.infrastructure.driving.rest.dto;

import com.example.mybank.domain.model.Client;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A client")
public record ClientDTO(
        @Schema(description = "Id", example = "01KAV7BPA02WS4YDPFGYGZG3VF")
        String id,

        @Schema(description = "Last name", example = "Dupont")
        String lastName,

        @Schema(description = "First name", example = "Dupont")
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