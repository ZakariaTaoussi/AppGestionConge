package com.example.backend.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
