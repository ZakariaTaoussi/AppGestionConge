package com.example.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    protected Email() {
        // Required by JPA.
    }

    public Email(String value) {
        this.value = normalizeAndValidate(value);
    }

    public String getValue() {
        return value;
    }

    private String normalizeAndValidate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        String normalizedEmail = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Format d'email invalide");
        }

        return normalizedEmail;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Email email)) {
            return false;
        }
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
