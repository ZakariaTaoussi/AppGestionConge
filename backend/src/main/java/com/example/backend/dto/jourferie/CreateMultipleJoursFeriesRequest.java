package com.example.backend.dto.jourferie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateMultipleJoursFeriesRequest(
        @Valid
        @NotEmpty(message = "La liste des jours feries est obligatoire")
        List<CreateJourFerieRequest> joursFeries
) {
}
