import { Utilisateur } from './utilisateur.model';

export interface LoginRequest {
  email: string;
  password: string;
  remember?: boolean;
}

export interface LoginResponse {
  utilisateur: Utilisateur;
}

export interface SetupPasswordRequest {
  token: string;
  password: string;
}
