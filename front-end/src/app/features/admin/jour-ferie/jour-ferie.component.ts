import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable, finalize } from 'rxjs';
import { Agenda } from '../models/agenda.model';
import { JourCalendrier } from '../models/jour-calendrier.model';
import { CreateJourFerieRequest, JourFerie } from '../models/jour-ferie.model';
import { AdminAgendaService } from '../services/admin-agenda.service';
import { AdminJourFerieService } from '../services/admin-jour-ferie.service';

@Component({
  selector: 'app-admin-jour-ferie',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './jour-ferie.component.html',
  styleUrls: ['./jour-ferie.component.scss'],
})
export class AdminJourFerieComponent implements OnInit {
  private readonly agendaService = inject(AdminAgendaService);
  private readonly jourFerieService = inject(AdminJourFerieService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly currentYear = new Date().getFullYear();
  readonly weekdays = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
  readonly agendas = signal<Agenda[]>([]);
  readonly selectedAgendaId = signal<number | null>(null);
  readonly joursCalendrier = signal<JourCalendrier[]>([]);
  readonly joursFeries = signal<JourFerie[]>([]);
  readonly displayedMonth = signal(new Date().getMonth());
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly isSavingAgenda = signal(false);
  readonly errorMessage = signal('');
  readonly showModal = signal(false);

  editingId?: number;
  agendaForm = { annee: this.currentYear };
  modalForms: HolidayForm[] = [this.emptyForm()];

  readonly selectedAgenda = computed(() =>
    this.agendas().find(agenda => agenda.id === this.selectedAgendaId()) ?? null
  );
  readonly calendarDays = computed(() => this.buildCalendarDays());
  readonly monthLabel = computed(() => {
    const annee = this.selectedAgenda()?.annee ?? this.currentYear;
    return new Intl.DateTimeFormat('fr-FR', { month: 'long', year: 'numeric' })
      .format(new Date(annee, this.displayedMonth(), 1));
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.chargerAgendas();
    }
  }

  chargerAgendas(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);
    this.agendaService.getAgendas()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: agendas => {
          const sorted = [...agendas].sort((a, b) => a.annee - b.annee);
          this.agendas.set(sorted);
          if (!this.selectedAgendaId() && sorted.length) {
            this.selectedAgendaId.set(sorted[0].id);
            this.displayedMonth.set(0);
          }
          this.chargerDonneesAgenda();
        },
        error: () => this.errorMessage.set('Impossible de charger les calendriers.'),
      });
  }

  creerAgenda(): void {
    if (this.agendaForm.annee < this.currentYear) {
      this.errorMessage.set(`L'annee doit etre superieure ou egale a ${this.currentYear}.`);
      return;
    }
    this.errorMessage.set('');
    this.isSavingAgenda.set(true);
    this.agendaService.createAgenda({ annee: Number(this.agendaForm.annee) })
      .pipe(finalize(() => this.isSavingAgenda.set(false)))
      .subscribe({
        next: agenda => {
          this.agendas.update(agendas => [...agendas, agenda].sort((a, b) => a.annee - b.annee));
          this.selectedAgendaId.set(agenda.id);
          this.displayedMonth.set(0);
          this.chargerDonneesAgenda();
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible de creer l'annee.")),
      });
  }

  selectionnerAgenda(value: string | number | null): void {
    const agendaId = Number(value);
    this.selectedAgendaId.set(Number.isFinite(agendaId) && agendaId > 0 ? agendaId : null);
    this.displayedMonth.set(0);
    this.reinitialiser();
    this.chargerDonneesAgenda();
  }

  chargerDonneesAgenda(): void {
    const agendaId = this.selectedAgendaId();
    if (!agendaId) {
      this.joursCalendrier.set([]);
      this.joursFeries.set([]);
      return;
    }
    this.errorMessage.set('');
    this.isLoading.set(true);
    this.agendaService.getJoursCalendrier(agendaId).subscribe({
      next: jours => this.joursCalendrier.set(jours),
      error: () => this.errorMessage.set('Impossible de charger les jours du calendrier.'),
    });
    this.jourFerieService.getJoursFeriesByAgenda(agendaId)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: joursFeries => this.joursFeries.set(joursFeries),
        error: () => this.errorMessage.set('Impossible de charger les jours feries.'),
      });
  }

  moisPrecedent(): void {
    this.displayedMonth.update(month => month === 0 ? 11 : month - 1);
  }

  moisSuivant(): void {
    this.displayedMonth.update(month => month === 11 ? 0 : month + 1);
  }

  ouvrirAjout(date?: string): void {
    this.editingId = undefined;
    const form = this.emptyForm();
    if (date) {
      form.dateDebut = date;
      form.dateFin = date;
    }
    this.modalForms = [form];
    this.showModal.set(true);
  }

  modifier(jour: JourFerie): void {
    this.editingId = jour.id;
    this.modalForms = [{
      nom: jour.nom,
      dateDebut: jour.dateDebut,
      dateFin: jour.dateFin,
      description: jour.description,
      couleur: 'Blue',
    }];
    this.showModal.set(true);
  }

  fermerModal(): void {
    this.showModal.set(false);
    this.reinitialiser();
  }

  enregistrer(): void {
    const jourFerie = this.buildRequest(this.modalForms[0]);
    if (!jourFerie) return;
    this.errorMessage.set('');
    this.isSaving.set(true);
    const operation: Observable<JourFerie> = this.editingId
      ? this.jourFerieService.updateJourFerie(this.editingId, jourFerie)
      : this.jourFerieService.createJourFerie(jourFerie);

    operation.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: () => {
        this.fermerModal();
        this.chargerDonneesAgenda();
      },
      error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible d'enregistrer ce jour ferie.")),
    });
  }

  supprimer(jour: JourFerie): void {
    this.supprimerParId(jour.id);
  }

  supprimerDepuisModal(): void {
    if (!this.editingId) return;
    this.supprimerParId(this.editingId, true);
  }

  modifierDepuisCalendrier(jour: JourFerie, event: MouseEvent): void {
    event.stopPropagation();
    this.modifier(jour);
  }

  private supprimerParId(id: number, closeModal = false): void {
    this.errorMessage.set('');
    this.isSaving.set(true);
    this.jourFerieService.deleteJourFerie(id)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: () => {
          if (closeModal) {
            this.fermerModal();
          }
          this.chargerDonneesAgenda();
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de supprimer ce jour ferie.')),
      });
  }

  getJoursFeriesForDate(date: string): JourFerie[] {
    return this.joursFeries().filter(jour => jour.dateDebut <= date && jour.dateFin >= date);
  }

  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' }).format(new Date(date));
  }

  private buildCalendarDays(): CalendarDay[] {
    const agenda = this.selectedAgenda();
    if (!agenda) return [];
    const month = this.displayedMonth();
    const first = new Date(agenda.annee, month, 1);
    const start = new Date(first);
    start.setDate(first.getDate() - first.getDay());
    return Array.from({ length: 42 }, (_, index) => {
      const date = new Date(start);
      date.setDate(start.getDate() + index);
      const iso = this.toIsoDate(date);
      return {
        date: iso,
        day: date.getDate(),
        inMonth: date.getMonth() === month,
        isToday: iso === this.toIsoDate(new Date()),
        joursFeries: this.getJoursFeriesForDate(iso),
      };
    });
  }

  private buildRequest(form: HolidayForm): CreateJourFerieRequest | null {
    const agendaId = this.selectedAgendaId();
    if (!agendaId) {
      this.errorMessage.set('Selectionnez ou creez une annee avant d\'ajouter un jour ferie.');
      return null;
    }
    if (!form.nom.trim()) {
      this.errorMessage.set('Le nom du jour ferie est obligatoire.');
      return null;
    }
    if (!form.dateDebut || !form.dateFin) {
      this.errorMessage.set('Les dates de debut et de fin sont obligatoires.');
      return null;
    }
    if (form.dateFin < form.dateDebut) {
      this.errorMessage.set('La date de fin doit etre posterieure ou egale a la date de debut.');
      return null;
    }
    return {
      nom: form.nom.trim(),
      dateDebut: form.dateDebut,
      dateFin: form.dateFin,
      description: form.description?.trim() || null,
      agendaId,
    };
  }

  private reinitialiser(): void {
    this.editingId = undefined;
    this.modalForms = [this.emptyForm()];
  }

  private emptyForm(): HolidayForm {
    return { nom: '', dateDebut: '', dateFin: '', description: null, couleur: 'Blue' };
  }

  private toIsoDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    return error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
      ? error.error.message
      : fallback;
  }
}

interface HolidayForm {
  nom: string;
  dateDebut: string;
  dateFin: string;
  description: string | null;
  couleur: string;
}

interface CalendarDay {
  date: string;
  day: number;
  inMonth: boolean;
  isToday: boolean;
  joursFeries: JourFerie[];
}
