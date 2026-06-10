import { Role } from '../enums/role.enum';
import { Utilisateur } from './utilisateur.model';

export interface LoginRequest {
  email: string;
  password: string;
  remember?: boolean;
}

export interface BackendAuthResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: Role;
  message: string;
}

export interface LoginResponse {
  utilisateur: Utilisateur;
  message: string;
}

export interface SetupPasswordRequest {
  token: string;
  password: string;
}
