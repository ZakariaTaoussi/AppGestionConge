package com.example.backend.service.implementations;

import com.example.backend.dto.departement.AffecterResponsableRequest;
import com.example.backend.dto.departement.CreateDepartementRequest;
import com.example.backend.dto.departement.DepartementResponse;
import com.example.backend.dto.departement.UpdateDepartementRequest;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DepartementMapper;
import com.example.backend.model.Departement;
import com.example.backend.model.Responsable;
import com.example.backend.repository.DepartementRepository;
import com.example.backend.service.interfaces.IDepartementService;
import com.example.backend.service.interfaces.IResponsableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartementServiceImpl implements IDepartementService {

    private final DepartementRepository departementRepository;
    private final IResponsableService responsableService;
    private final DepartementMapper departementMapper;

    @Override
    public DepartementResponse creerDepartement(CreateDepartementRequest request) {
        String nom = request.nom().trim();
        verifierNomDisponible(nom, null);
        Departement departement = Departement.builder()
                .nom(nom)
                .responsable(getResponsableOrNull(request.responsableId()))
                .build();
        return departementMapper.toResponse(departementRepository.save(departement));
    }

    @Override
    public DepartementResponse modifierDepartement(Long id, UpdateDepartementRequest request) {
        Departement departement = getDepartement(id);
        String nom = request.nom().trim();
        verifierNomDisponible(nom, id);
        departement.setNom(nom);
        departement.setResponsable(getResponsableOrNull(request.responsableId()));
        return departementMapper.toResponse(departementRepository.save(departement));
    }

    @Override
    public void supprimerDepartement(Long id) {
        Departement departement = getDepartement(id);
        departementRepository.delete(departement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartementResponse> getAllDepartements() {
        return departementRepository.findAll().stream()
                .map(departementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartementResponse getDepartementById(Long id) {
        return departementMapper.toResponse(getDepartement(id));
    }

    @Override
    public DepartementResponse affecterResponsable(Long departementId, AffecterResponsableRequest request) {
        Departement departement = getDepartement(departementId);
        departement.setResponsable(getResponsable(request.responsableId()));
        return departementMapper.toResponse(departementRepository.save(departement));
    }

    private Departement getDepartement(Long id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departement introuvable"));
    }

    private Responsable getResponsableOrNull(Long responsableId) {
        return responsableId == null ? null : responsableService.convertirEnResponsable(responsableId);
    }

    private Responsable getResponsable(Long responsableId) {
        return responsableService.convertirEnResponsable(responsableId);
    }

    private void verifierNomDisponible(String nom, Long departementId) {
        departementRepository.findByNom(nom).ifPresent(existing -> {
            if (!existing.getId().equals(departementId)) {
                throw new ConflictException("Nom de departement deja utilise");
            }
        });
    }
}
