import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  CreateJourFerieRequest,
  CreateMultipleJoursFeriesRequest,
  JourFerie,
} from '../models/jour-ferie.model';

@Injectable({ providedIn: 'root' })
export class AdminJourFerieService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/jours-feries`;

  getJoursFeries(): Observable<JourFerie[]> {
    return this.http.get<JourFerie[]>(this.baseUrl);
  }

  getJoursFeriesByAgenda(agendaId: number): Observable<JourFerie[]> {
    return this.http.get<JourFerie[]>(`${this.baseUrl}/agenda/${agendaId}`);
  }

  createJourFerie(request: CreateJourFerieRequest): Observable<JourFerie> {
    return this.http.post<JourFerie>(this.baseUrl, request);
  }

  createMultipleJoursFeries(request: CreateMultipleJoursFeriesRequest): Observable<JourFerie[]> {
    return this.http.post<JourFerie[]>(`${this.baseUrl}/multiple`, request);
  }

  updateJourFerie(id: number, request: CreateJourFerieRequest): Observable<JourFerie> {
    return this.http.put<JourFerie>(`${this.baseUrl}/${id}`, request);
  }

  deleteJourFerie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
