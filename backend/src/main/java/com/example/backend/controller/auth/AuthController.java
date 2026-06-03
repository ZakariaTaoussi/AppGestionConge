package com.example.backend.controller.auth;

import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.dto.auth.LoginResponse;
import com.example.backend.dto.auth.SetupPasswordRequest;
import com.example.backend.service.auth.AuthService;
import com.example.backend.service.auth.PasswordSetupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordSetupService passwordSetupService;
    private final String cookieName;
    private final boolean cookieSecure;

    public AuthController(
            AuthService authService,
            PasswordSetupService passwordSetupService,
            @Value("${app.jwt.cookie-name}") String cookieName,
            @Value("${app.jwt.cookie-secure}") boolean cookieSecure
    ) {
        this.authService = authService;
        this.passwordSetupService = passwordSetupService;
        this.cookieName = cookieName;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request);
        ResponseCookie cookie = createJwtCookie(result.token(), Duration.ofDays(1));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.response());
    }

    @PostMapping("/setup-password")
    public ResponseEntity<Void> setupPassword(@RequestBody SetupPasswordRequest request) {
        passwordSetupService.setupPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = createJwtCookie("", Duration.ZERO);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie createJwtCookie(String token, Duration maxAge) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }
}
