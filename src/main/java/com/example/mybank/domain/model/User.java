package com.example.mybank.domain.model;

import com.github.f4b6a3.ulid.Ulid;

import java.util.Objects;

public record User(Id id, Login login, Password password, Client.Id clientId) {
    public User {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(clientId, "clientId is required");
        Objects.requireNonNull(login, "login is required");
        Objects.requireNonNull(password, "password is required");
    }

    public record Login(String value) {
        public Login {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Login cannot be null or blank");
            }
            value = value.toLowerCase();
        }
    }

    public record Password(String value) {
        public Password {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
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
