export interface AdminRegleConge {
  id: number | null;
  jourAjouteParMois: number;
  preavisJours: number;
  maxJoursConsecutifs: number;
  reportMaxJours: number;
  createdAt: string | null;
  updatedAt: string | null;
}

export type AdminRegleCongeRequest = Pick<
  AdminRegleConge,
  'jourAjouteParMois' | 'preavisJours' | 'maxJoursConsecutifs' | 'reportMaxJours'
>;
