package com.example.backend.dto.auth;

public record SetupPasswordRequest(
        String token,
        String password
) {
}
