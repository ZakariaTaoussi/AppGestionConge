package com.example.backend.dto.auth;

import com.example.backend.dto.utilisateur.UtilisateurResponse;

public record LoginResponse(
        UtilisateurResponse utilisateur
) {
}
