import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ResponsableCandidat } from '../models/responsable-candidat.model';

@Injectable({ providedIn: 'root' })
export class AdminResponsableService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/responsables`;

  getCandidatsResponsables(): Observable<ResponsableCandidat[]> {
    return this.http.get<ResponsableCandidat[]>(`${this.baseUrl}/candidats`);
  }
}
