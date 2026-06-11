import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AffecterResponsableRequest,
  CreateDepartementRequest,
  Departement,
  UpdateDepartementRequest,
} from '../models/departement.model';

@Injectable({ providedIn: 'root' })
export class AdminDepartementService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin/departements`;

  getDepartements(): Observable<Departement[]> {
    return this.http.get<Departement[]>(this.baseUrl);
  }

  getDepartement(id: number): Observable<Departement> {
    return this.http.get<Departement>(`${this.baseUrl}/${id}`);
  }

  createDepartement(request: CreateDepartementRequest): Observable<Departement> {
    return this.http.post<Departement>(this.baseUrl, request);
  }

  updateDepartement(id: number, request: UpdateDepartementRequest): Observable<Departement> {
    return this.http.put<Departement>(`${this.baseUrl}/${id}`, request);
  }

  deleteDepartement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  affecterResponsable(id: number, request: AffecterResponsableRequest): Observable<Departement> {
    return this.http.put<Departement>(`${this.baseUrl}/${id}/responsable`, request);
  }

}
