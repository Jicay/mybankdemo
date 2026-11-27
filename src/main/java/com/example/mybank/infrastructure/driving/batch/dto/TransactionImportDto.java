package com.example.mybank.infrastructure.driving.batch.dto;

public record TransactionImportDto(
        String accountId,
        Long amountCents,
        String type,
        String date
) {
}
