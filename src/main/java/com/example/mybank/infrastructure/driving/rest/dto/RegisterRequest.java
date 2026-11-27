package com.example.mybank.infrastructure.driving.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String login, @NotBlank String lastName, @NotBlank String firstName,
                              @NotBlank String password) {
}