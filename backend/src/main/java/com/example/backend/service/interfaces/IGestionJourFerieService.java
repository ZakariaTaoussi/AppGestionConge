package com.example.backend.service.interfaces;

import com.example.backend.dto.jourferie.CreateJourFerieRequest;
import com.example.backend.dto.jourferie.CreateMultipleJoursFeriesRequest;
import com.example.backend.dto.jourferie.JourFerieResponse;
import java.util.List;

public interface IGestionJourFerieService {
    JourFerieResponse creerJourFerie(CreateJourFerieRequest request);

    List<JourFerieResponse> creerPlusieursJoursFeries(CreateMultipleJoursFeriesRequest request);

    JourFerieResponse modifierJourFerie(Long id, CreateJourFerieRequest request);

    void supprimerJourFerie(Long id);

    List<JourFerieResponse> getJoursFeriesByAgenda(Long agendaId);

    List<JourFerieResponse> getAllJoursFeries();
}
