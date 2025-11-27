package com.example.mybank.infrastructure.driving.rest.dto;

import com.example.mybank.domain.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A bank account")
public record AccountDTO(
        @Schema(description = "Id", example = "01KAV7BPA02WS4YDPFGYGZG3VF")
        String id,
        @Schema(description = "Name of the account", example = "Compte courant")
        String name,
        @Schema(description = "Type of the account", example = "COMPTE_COURANT",
                allowableValues = {" COMPTE_COURANT", "LIVRET_A", "LDD", "PEA", "CTO", "PEL"})
        String type,
        @Schema(description = "Amount in cents of the account", example = "1000")
        long amountCents
) {
    public static AccountDTO fromDomain(Account account) {
        return new AccountDTO(
                account.id().value().toString(),
                account.name().value(),
                account.type().name(),
                account.amount().value()
        );
    }
}
