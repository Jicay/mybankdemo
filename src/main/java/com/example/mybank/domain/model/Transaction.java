package com.example.mybank.domain.model;

import com.github.f4b6a3.ulid.Ulid;

import java.time.Instant;
import java.util.Objects;

public record Transaction(
        Id id,
        Account.Id accountId,
        Amount amount,
        Type type,
        Instant date
) {
    public Transaction {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(accountId, "accountId is required");
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(date, "date is required");
    }

    public enum Type {
        DEBIT,
        CREDIT
    }

    public record Id(Ulid value) {
        public Id() {
            this(Ulid.fast());
        }

        public Id {
            if (value == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
        }

        public static Id from(String ulid) {
            return new Id(Ulid.from(ulid));
        }
    }
}
