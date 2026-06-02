package com.example.backend.service.admin;

import com.example.backend.domain.JourFerie;
import com.example.backend.dto.admin.AdminJourFerieRequest;
import com.example.backend.dto.admin.AdminJourFerieResponse;
import com.example.backend.dto.admin.AdminPageResponse;
import com.example.backend.exception.jourferie.JourFerieNonTrouveException;
import com.example.backend.repository.JourFerieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AdminServiceJourFerie {

    private static final int MAX_PAGE_SIZE = 100;

    private final JourFerieRepository jourFerieRepository;

    public AdminServiceJourFerie(JourFerieRepository jourFerieRepository) {
        this.jourFerieRepository = jourFerieRepository;
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminJourFerieResponse> getJoursFeries(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numero de page doit etre positif ou nul");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("La taille de page doit etre comprise entre 1 et " + MAX_PAGE_SIZE);
        }

        Page<JourFerie> joursFeries = jourFerieRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        return new AdminPageResponse<>(
                joursFeries.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                joursFeries.getNumber(),
                joursFeries.getSize(),
                joursFeries.getTotalElements(),
                joursFeries.getTotalPages()
        );
    }

    @Transactional
    public AdminJourFerieResponse createJourFerie(AdminJourFerieRequest request) {
        JourFerie jourFerie = new JourFerie(
                requiredDate(request.dateDebut(), "La date de debut est obligatoire"),
                requiredDate(request.dateFin(), "La date de fin est obligatoire"),
                normalizeDescription(request.description())
        );
        validateDates(jourFerie.getDateDebut(), jourFerie.getDateFin());

        return toResponse(jourFerieRepository.save(jourFerie));
    }

    @Transactional
    public AdminJourFerieResponse updateJourFerie(Long id, AdminJourFerieRequest request) {
        JourFerie jourFerie = getJourFerie(id);
        LocalDate dateDebut = requiredDate(request.dateDebut(), "La date de debut est obligatoire");
        LocalDate dateFin = requiredDate(request.dateFin(), "La date de fin est obligatoire");
        validateDates(dateDebut, dateFin);

        jourFerie.setDateDebut(dateDebut);
        jourFerie.setDateFin(dateFin);
        jourFerie.setDescription(normalizeDescription(request.description()));

        return toResponse(jourFerieRepository.save(jourFerie));
    }

    @Transactional
    public void deleteJourFerie(Long id) {
        jourFerieRepository.delete(getJourFerie(id));
    }

    private JourFerie getJourFerie(Long id) {
        return jourFerieRepository.findById(id)
                .orElseThrow(() -> new JourFerieNonTrouveException(id));
    }

    private AdminJourFerieResponse toResponse(JourFerie jourFerie) {
        return new AdminJourFerieResponse(
                jourFerie.getId(),
                jourFerie.getDateDebut(),
                jourFerie.getDateFin(),
                jourFerie.getDescription()
        );
    }

    private LocalDate requiredDate(LocalDate date, String message) {
        if (date == null) {
            throw new IllegalArgumentException(message);
        }
        return date;
    }

    private void validateDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure ou egale a la date de debut");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalizedDescription = description.trim();
        if (normalizedDescription.length() > 255) {
            throw new IllegalArgumentException("La description ne doit pas depasser 255 caracteres");
        }
        return normalizedDescription;
    }
}
