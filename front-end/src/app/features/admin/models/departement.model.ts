export interface Departement {
  id: number;
  nom: string;
  responsableId: number | null;
  responsableNom: string | null;
  responsablePrenom: string | null;
}

export interface CreateDepartementRequest {
  nom: string;
  responsableId: number | null;
}

export type UpdateDepartementRequest = CreateDepartementRequest;

export interface AffecterResponsableRequest {
  responsableId: number;
}
