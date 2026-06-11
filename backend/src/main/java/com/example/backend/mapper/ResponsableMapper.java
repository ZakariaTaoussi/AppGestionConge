package com.example.backend.mapper;

import com.example.backend.dto.departement.ResponsableResponse;
import com.example.backend.model.Responsable;
import org.springframework.stereotype.Component;

@Component
public class ResponsableMapper {

    public ResponsableResponse toResponse(Responsable responsable) {
        return new ResponsableResponse(
                responsable.getId(),
                responsable.getNom(),
                responsable.getPrenom(),
                responsable.getEmail(),
                responsable.getRole()
        );
    }
}
