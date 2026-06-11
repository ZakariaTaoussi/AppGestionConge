package com.example.backend.mapper;

import com.example.backend.dto.departement.DepartementResponse;
import com.example.backend.model.Departement;
import com.example.backend.model.Responsable;
import org.springframework.stereotype.Component;

@Component
public class DepartementMapper {

    public DepartementResponse toResponse(Departement departement) {
        Responsable responsable = departement.getResponsable();
        return new DepartementResponse(
                departement.getId(),
                departement.getNom(),
                responsable != null ? responsable.getId() : null,
                responsable != null ? responsable.getNom() : null,
                responsable != null ? responsable.getPrenom() : null
        );
    }
}
