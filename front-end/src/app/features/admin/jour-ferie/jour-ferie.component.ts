import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AdminJourFerie, AdminJourFerieRequest } from '../models/admin-jour-ferie.model';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-jour-ferie',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './jour-ferie.component.html',
  styleUrls: ['./jour-ferie.component.scss'],
})
export class AdminJourFerieComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly platformId = inject(PLATFORM_ID);

  editingId?: number;
  form = this.emptyForm();
  readonly joursFeries = signal<AdminJourFerie[]>([]);
  readonly joursTries = computed(() =>
    [...this.joursFeries()].sort((a, b) => a.dateDebut.localeCompare(b.dateDebut))
  );
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly currentPage = signal(0);
  readonly pageSize = 10;
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.chargerJoursFeries();
    }
  }

  chargerJoursFeries(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);

    this.adminService
      .getJoursFeries(this.currentPage(), this.pageSize)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: response => {
          this.joursFeries.set(response.content);
          this.currentPage.set(response.page);
          this.totalElements.set(response.totalElements);
          this.totalPages.set(response.totalPages);
        },
        error: () => this.errorMessage.set('Impossible de charger les jours feries.'),
      });
  }

  enregistrer(): void {
    if (!this.form.dateDebut || !this.form.dateFin) {
      this.errorMessage.set('Les dates de debut et de fin sont obligatoires.');
      return;
    }
    if (this.form.dateFin < this.form.dateDebut) {
      this.errorMessage.set('La date de fin doit etre posterieure ou egale a la date de debut.');
      return;
    }

    this.errorMessage.set('');
    this.isSaving.set(true);
    const request = {
      ...this.form,
      description: this.form.description?.trim() || null,
    };
    const operation = this.editingId
      ? this.adminService.updateJourFerie(this.editingId, request)
      : this.adminService.createJourFerie(request);

    operation
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: jourFerie => {
          if (this.editingId) {
            this.remplacerJourFerie(jourFerie);
          } else {
            this.ajouterJourFerie(jourFerie);
          }
          this.reinitialiser();
        },
        error: () => this.errorMessage.set("Impossible d'enregistrer ce jour ferie."),
      });
  }

  modifier(jour: AdminJourFerie): void {
    this.editingId = jour.id;
    this.form = { description: jour.description, dateDebut: jour.dateDebut, dateFin: jour.dateFin };
  }

  supprimer(jour: AdminJourFerie): void {
    this.errorMessage.set('');
    this.adminService.deleteJourFerie(jour.id).subscribe({
      next: () => {
        if (this.editingId === jour.id) {
          this.reinitialiser();
        }
        this.joursFeries.update(joursFeries => joursFeries.filter(item => item.id !== jour.id));
        this.totalElements.update(totalElements => Math.max(totalElements - 1, 0));
        this.totalPages.set(Math.ceil(this.totalElements() / this.pageSize));

        if (this.currentPage() >= this.totalPages() && this.currentPage() > 0) {
          this.currentPage.update(currentPage => currentPage - 1);
        }
        this.chargerJoursFeries();
      },
      error: () => this.errorMessage.set('Impossible de supprimer ce jour ferie.'),
    });
  }

  reinitialiser(): void {
    this.editingId = undefined;
    this.form = this.emptyForm();
  }

  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' }).format(new Date(date));
  }

  getDuree(jour: Pick<AdminJourFerie, 'dateDebut' | 'dateFin'>): number {
    const debut = new Date(jour.dateDebut);
    const fin = new Date(jour.dateFin);
    return Math.max(Math.floor((fin.getTime() - debut.getTime()) / 86_400_000) + 1, 1);
  }

  getAnnee(jour: AdminJourFerie): number {
    return new Date(jour.dateDebut).getFullYear();
  }

  pagePrecedente(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(currentPage => currentPage - 1);
      this.chargerJoursFeries();
    }
  }

  pageSuivante(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.currentPage.update(currentPage => currentPage + 1);
      this.chargerJoursFeries();
    }
  }

  private ajouterJourFerie(jourFerie: AdminJourFerie): void {
    const ajoutDepuisPremierePage = this.currentPage() === 0;
    this.currentPage.set(0);
    this.totalElements.update(totalElements => totalElements + 1);
    this.totalPages.set(Math.ceil(this.totalElements() / this.pageSize));
    this.joursFeries.update(joursFeries =>
      [jourFerie, ...(ajoutDepuisPremierePage ? joursFeries : [])].slice(0, this.pageSize)
    );

    if (!ajoutDepuisPremierePage) {
      this.chargerJoursFeries();
    }
  }

  private remplacerJourFerie(jourFerie: AdminJourFerie): void {
    this.joursFeries.update(joursFeries =>
      joursFeries.map(item => item.id === jourFerie.id ? jourFerie : item)
    );
  }

  private emptyForm(): AdminJourFerieRequest {
    return { description: null, dateDebut: '', dateFin: '' };
  }
}
