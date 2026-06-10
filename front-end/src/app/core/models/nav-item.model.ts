import { Role } from '../enums/role.enum';

export interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles: Role[];
}
