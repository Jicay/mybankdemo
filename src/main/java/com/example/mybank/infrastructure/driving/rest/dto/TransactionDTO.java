package com.example.mybank.infrastructure.driving.rest.dto;

import com.example.mybank.domain.model.Transaction;

import java.time.Instant;

public record TransactionDTO(
        String id,
        String accountId,
        long amountCents,
        String type,
        Instant date
) {
    public static TransactionDTO fromDomain(Transaction t) {
        return new TransactionDTO(
                t.id().value().toString(),
                t.accountId().value().toString(),
                t.amount().value(),
                t.type().name(),
                t.date()
        );
    }
}
