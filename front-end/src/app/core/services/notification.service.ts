import { Injectable, signal } from '@angular/core';

export type NotificationKind = 'info' | 'success' | 'warning';

export interface NotificationItem {
  id: number;
  title: string;
  date: string;
  kind: NotificationKind;
  read: boolean;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly notifications = signal<NotificationItem[]>([
    {
      id: 1,
      title: 'Bienvenue dans votre espace SOPHATEL.',
      date: 'Aujourd hui',
      kind: 'info',
      read: false,
    },
  ]);

  getNotifications(): NotificationItem[] {
    return this.notifications();
  }

  getUnreadCount(): number {
    return this.notifications().filter(notification => !notification.read).length;
  }

  markAllAsRead(): void {
    this.notifications.update(notifications =>
      notifications.map(notification => ({ ...notification, read: true })),
    );
  }

  add(title: string, kind: NotificationKind = 'info'): void {
    const notification: NotificationItem = {
      id: Date.now(),
      title,
      date: 'A l instant',
      kind,
      read: false,
    };

    this.notifications.update(notifications => [notification, ...notifications]);
  }
}
