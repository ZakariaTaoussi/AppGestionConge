import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Agenda, CreateAgendaRequest } from '../models/agenda.model';
import { JourCalendrier } from '../models/jour-calendrier.model';

@Injectable({ providedIn: 'root' })
export class AdminAgendaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/agendas`;

  getAgendas(): Observable<Agenda[]> {
    return this.http.get<Agenda[]>(this.baseUrl);
  }

  getAgenda(id: number): Observable<Agenda> {
    return this.http.get<Agenda>(`${this.baseUrl}/${id}`);
  }

  createAgenda(request: CreateAgendaRequest): Observable<Agenda> {
    return this.http.post<Agenda>(this.baseUrl, request);
  }

  deleteAgenda(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getJoursCalendrier(agendaId: number): Observable<JourCalendrier[]> {
    return this.http.get<JourCalendrier[]>(`${this.baseUrl}/${agendaId}/jours-calendrier`);
  }
}
