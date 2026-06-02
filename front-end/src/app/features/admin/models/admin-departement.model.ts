import { Role } from '../../../core/enums/role.enum';

export interface AdminResponsable {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: Role;
}

export interface AdminDepartement {
  id: number;
  nom: string;
  responsable: AdminResponsable | null;
}

export interface AdminDepartementRequest {
  nom: string;
  responsableId: number | null;
}
