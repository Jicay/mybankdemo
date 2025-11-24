package com.example.mybank.infrastructure.driving.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Creation client request")
public record CreateClientRequest(
        @Schema(description = "Last name", example = "Dupont")
        @NotBlank(message = "lastName is required")
        String lastName,

        @Schema(description = "First name", example = "Jean")
        @NotBlank(message = "firstName is required") String firstName
) {
}