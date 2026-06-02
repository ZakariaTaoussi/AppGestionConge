import { Role } from '../../../core/enums/role.enum';

export interface AdminProfil {
  nom: string;
  prenom: string;
  role: Role;
  departement: string | null;
  email: string;
  responsable: string | null;
}
