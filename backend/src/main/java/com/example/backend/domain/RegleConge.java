package com.example.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "regles_conge")
public class RegleConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regle")
    private Long id;

    @Column(name = "jour_ajoute_par_mois", nullable = false)
    private Integer jourAjouteParMois;

    @Column(name = "preavis_jours", nullable = false)
    private Integer preavisJours;

    @Column(name = "report_max_jours", nullable = false)
    private Integer reportMaxJours;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RegleConge() {
        // Required by JPA.
    }

    public RegleConge(Integer jourAjouteParMois, Integer preavisJours, Integer reportMaxJours) {
        this.jourAjouteParMois = jourAjouteParMois;
        this.preavisJours = preavisJours;
        this.reportMaxJours = reportMaxJours;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getJourAjouteParMois() {
        return jourAjouteParMois;
    }

    public void setJourAjouteParMois(Integer jourAjouteParMois) {
        this.jourAjouteParMois = jourAjouteParMois;
    }

    public Integer getPreavisJours() {
        return preavisJours;
    }

    public void setPreavisJours(Integer preavisJours) {
        this.preavisJours = preavisJours;
    }

    public Integer getReportMaxJours() {
        return reportMaxJours;
    }

    public void setReportMaxJours(Integer reportMaxJours) {
        this.reportMaxJours = reportMaxJours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
