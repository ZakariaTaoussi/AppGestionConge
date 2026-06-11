package com.example.backend.service.implementations;

import com.example.backend.dto.auth.AuthResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.utilisateur.UtilisateurResponse;
import com.example.backend.exception.InvalidCredentialsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.UtilisateurMapper;
import com.example.backend.model.Utilisateur;
import com.example.backend.repository.UtilisateurRepository;
import com.example.backend.security.JwtService;
import com.example.backend.service.interfaces.IAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final JwtService jwtService;

    @Value("${app.jwt.cookie-name:access_token}")
    private String cookieName;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException("Mauvais identifiants");
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));

        String jwt = jwtService.generateToken(utilisateur.getEmail());
        addAccessTokenCookie(response, jwt);

        return new AuthResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                "Login reussi"
        );
    }

    @Override
    public void logout(HttpServletResponse response) {
        clearAccessTokenCookie(response);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UtilisateurResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .map(utilisateurMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));
    }

    private void addAccessTokenCookie(HttpServletResponse response, String jwt) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtExpirationMs / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
