package com.example.backend.service.interfaces;

import com.example.backend.dto.departement.AffecterResponsableRequest;
import com.example.backend.dto.departement.CreateDepartementRequest;
import com.example.backend.dto.departement.DepartementResponse;
import com.example.backend.dto.departement.UpdateDepartementRequest;
import java.util.List;

public interface IDepartementService {
    DepartementResponse creerDepartement(CreateDepartementRequest request);

    DepartementResponse modifierDepartement(Long id, UpdateDepartementRequest request);

    void supprimerDepartement(Long id);

    List<DepartementResponse> getAllDepartements();

    DepartementResponse getDepartementById(Long id);

    DepartementResponse affecterResponsable(Long departementId, AffecterResponsableRequest request);
}
