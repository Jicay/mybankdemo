package com.example.mybank.domain.model;

import com.github.f4b6a3.ulid.Ulid;

import java.util.Objects;

public record Client(Id id, Name lastName, Name firstName) {
    public Client {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(firstName, "firstName is required");
    }

    public record Name(String value) {
        public Name {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            value = value.trim();
        }
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
