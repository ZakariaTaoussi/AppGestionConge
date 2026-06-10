import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AdminRegleConge, AdminRegleCongeRequest } from '../models/admin-regle-conge.model';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-regle',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './regle.component.html',
  styleUrls: ['./regle.component.scss'],
})
export class AdminRegleComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly platformId = inject(PLATFORM_ID);

  form = this.emptyForm();
  readonly regleActive = signal<AdminRegleConge | null>(null);
  readonly isLoading = signal(false);
  readonly isSaving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.chargerRegleActive();
    }
  }

  get totalAnnuel(): number {
    return Number((this.form.jourAjouteParMois * 12).toFixed(1));
  }

  get projection() {
    return Array.from({ length: 12 }, (_, index) => ({
      mois: index + 1,
      total: Number((this.form.jourAjouteParMois * (index + 1)).toFixed(1)),
    }));
  }

  chargerRegleActive(): void {
    this.errorMessage.set('');
    this.isLoading.set(true);

    this.adminService
      .getRegleCongeActive()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: regle => {
          this.regleActive.set(regle);
          this.form = this.toForm(regle);
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, 'Impossible de charger la regle de conge.')),
      });
  }

  validerRegle(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.isSaving.set(true);

    this.adminService
      .createRegleConge(this.form)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: regle => {
          this.regleActive.set(regle);
          this.form = this.toForm(regle);
          this.successMessage.set('Regle validee');
        },
        error: error => this.errorMessage.set(this.getErrorMessage(error, "Impossible d'enregistrer cette regle.")),
      });
  }

  private emptyForm(): AdminRegleCongeRequest {
    return {
      jourAjouteParMois: 1.5,
      preavisJours: 15,
      maxJoursConsecutifs: 20,
      reportMaxJours: 5,
    };
  }

  private toForm(regle: AdminRegleConge): AdminRegleCongeRequest {
    return {
      jourAjouteParMois: regle.jourAjouteParMois,
      preavisJours: regle.preavisJours,
      maxJoursConsecutifs: regle.maxJoursConsecutifs,
      reportMaxJours: regle.reportMaxJours,
    };
  }

  private isFormValid(): boolean {
    if (this.form.jourAjouteParMois <= 0) {
      this.errorMessage.set('Le nombre de jours ajoutes par mois doit etre superieur a 0.');
      return false;
    }
    if (this.form.preavisJours < 0) {
      this.errorMessage.set('Le preavis doit etre positif ou nul.');
      return false;
    }
    if (this.form.maxJoursConsecutifs < 1) {
      this.errorMessage.set('Le maximum de jours consecutifs doit etre superieur a 0.');
      return false;
    }
    if (this.form.reportMaxJours < 0) {
      this.errorMessage.set('Le report autorise doit etre positif ou nul.');
      return false;
    }
    return true;
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    return error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
      ? error.error.message
      : fallback;
  }
}
