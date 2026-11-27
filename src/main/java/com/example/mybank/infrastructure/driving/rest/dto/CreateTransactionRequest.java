package com.example.mybank.infrastructure.driving.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateTransactionRequest(
        @NotBlank String accountId,
        long amountCents,
        @NotBlank String type,
        @NotNull Instant date
) {
}
