export interface AdminJourFerie {
  id: number;
  dateDebut: string;
  dateFin: string;
  description: string | null;
}

export type AdminJourFerieRequest = Omit<AdminJourFerie, 'id'>;

export interface AdminPage<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
