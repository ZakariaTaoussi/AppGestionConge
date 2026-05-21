import { Role } from '../enums/role.enum';

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  password?: string;
  role: Role;
}
