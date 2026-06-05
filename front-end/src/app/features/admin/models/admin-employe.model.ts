import { Role } from '../../../core/enums/role.enum';

export interface AdminEmploye {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: Role;
  departementId: number | null;
  departement: string | null;
  setupToken?: string | null;
}

export interface AdminCreateEmployeRequest {
  nom: string;
  prenom: string;
  email: string;
  role: Role;
  departementId: number;
}

export interface AdminUpdateEmployeRequest extends AdminCreateEmployeRequest {}
