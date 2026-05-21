package com.example.backend.web;

import com.example.backend.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final String cookieName;

    public AuthorizationInterceptor(JwtService jwtService, @Value("${app.jwt.cookie-name}") String cookieName) {
        this.jwtService = jwtService;
        this.cookieName = cookieName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Allow only login/logout public endpoints under /api/auth
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/logout")
                || path.startsWith("/auth/login") || path.startsWith("/auth/logout")) {
            return true;
        }

        // Determine required role for the path
        String requiredRole = requiredRoleForPath(path);
        if (requiredRole == null) {
            // no role required for this path
            return true;
        }

        // Read token from cookie
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            token = Arrays.stream(cookies)
                    .filter(c -> cookieName.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
        }

        if (token == null || token.isBlank() || !jwtService.isValid(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: missing or invalid token");
            return false;
        }

        String role = jwtService.extractRole(token);
        if (role == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: role not present in token");
            return false;
        }

        if (!role.equals(requiredRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: insufficient role");
            return false;
        }

        // attach user info for controllers if needed
        String email = jwtService.extractEmail(token);
        request.setAttribute("currentUserEmail", email);
        request.setAttribute("currentUserRole", role);

        return true;
    }

    private String requiredRoleForPath(String path) {
        String p = path.toLowerCase();

        // Normalize and extract the top-level segment that indicates the area (admin, employe, rh, ...)
        // Examples:
        // - /api/admin/employes  -> top = "admin"
        // - /employe/dashboard    -> top = "employe"
        String[] parts = p.split("/");
        String top = null;
        if (parts.length > 1) {
            if ("api".equals(parts[1]) && parts.length > 2) {
                top = parts[2];
            } else if (!parts[1].isBlank()) {
                top = parts[1];
            }
        }

        if (top == null) return null;

        switch (top) {
            case "employe":
            case "employes":
                return "EMPLOYE";
            case "admin":
                return "ADMIN";
            case "responsable":
                return "RESPONSABLE";
            case "rh":
                return "RH";
            case "directeur-general":
                return "DIRECTEUR_GENERAL";
            default:
                return null;
        }
    }
}
