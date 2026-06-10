package com.example.backend.service.interfaces;

import com.example.backend.dto.auth.AuthResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    UtilisateurResponse getCurrentUser();
}
