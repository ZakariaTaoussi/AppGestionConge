export interface JourFerie {
  id: number;
  nom: string;
  dateDebut: string;
  dateFin: string;
  description: string | null;
  agendaId: number;
  annee: number;
}

export interface CreateJourFerieRequest {
  nom: string;
  dateDebut: string;
  dateFin: string;
  description: string | null;
  agendaId: number;
  couleur?: string | null;
}

export interface CreateMultipleJoursFeriesRequest {
  joursFeries: CreateJourFerieRequest[];
}
