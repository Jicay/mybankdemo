package com.example.mybank.domain.model;

import com.github.f4b6a3.ulid.Ulid;

import java.util.Objects;

public record Account(
        Id id,
        Client.Id clientId,
        Name name,
        Type type,
        Amount amount
) {
    public Account {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(clientId, "clientId is required");
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(type, "type is required");
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

    public record Name(String value) {
        public Name {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            value = value.trim();
        }
    }

    public enum Type {
        COMPTE_COURANT,
        LIVRET_A,
        LDD,
        PEA,
        CTO,
        PEL
    }
}
