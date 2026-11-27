package com.example.mybank.infrastructure.driving.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTransactionRequest(
        @NotBlank String accountId,
        long amountCents,
        @NotBlank String type
) {
}
