package com.example.mybank.infrastructure.driving.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Creation account request")
public record CreateAccountRequest(
        @Schema(description = "Name of the account", example = "Compte courant")
        @NotBlank(message = "name is required")
        String name,

        @Schema(description = "Type of the account", example = "COMPTE_COURANT",
                allowableValues = {" COMPTE_COURANT", "LIVRET_A", "LDD", "PEA", "CTO", "PEL"})
        @NotNull(message = "type is required")
        String type,

        @Schema(description = "Amount in cents of the account", example = "1000")
        @Min(value = 0, message = "amountCents must be >= 0")
        long amountCents
) {
}
