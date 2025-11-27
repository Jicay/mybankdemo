package com.example.mybank.domain.model;

public record Amount(long value) {
    public static Amount fromCents(long cents) {
        return new Amount(cents);
    }
}
