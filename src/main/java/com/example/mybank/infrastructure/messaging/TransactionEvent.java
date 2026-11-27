package com.example.mybank.infrastructure.messaging;

import java.time.Instant;

public record TransactionEvent(
        String accountId,
        long amountCents,
        String type,
        Instant date
) {
}
