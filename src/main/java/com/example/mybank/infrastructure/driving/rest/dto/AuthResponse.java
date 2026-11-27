package com.example.mybank.infrastructure.driving.rest.dto;

public record AuthResponse(String token, String login, String clientId) {
}