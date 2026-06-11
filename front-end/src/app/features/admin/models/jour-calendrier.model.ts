export interface JourCalendrier {
  id: number;
  date: string;
  agendaId: number;
  jourFerieId: number | null;
  jourFerieNom: string | null;
}
