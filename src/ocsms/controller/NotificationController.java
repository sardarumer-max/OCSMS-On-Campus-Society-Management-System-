package ocsms.controller;

import ocsms.model.Notification;
import ocsms.service.NotificationService;
import ocsms.util.SessionManager;

import java.util.List;

/**
 * CONTROLLER (MVC) — NotificationController
 * Bridges the NotificationService to the UI panels.
 * Used in: UC-20
 */
public class NotificationController {

    /** Returns all notifications for the current logged-in user */
    public List<Notification> getMyNotifications() {
        String userId = SessionManager.getInstance().getCurrentUser().getId();
        return NotificationService.getInstance().getNotificationsForUser(userId);
    }

    /** Returns unread notification count for current user */
    public int getUnreadCount() {
        String userId = SessionManager.getInstance().getCurrentUser().getId();
        return NotificationService.getInstance().getUnreadCount(userId);
    }

    /** Marks all notifications as read for current user */
    public void markAllRead() {
        String userId = SessionManager.getInstance().getCurrentUser().getId();
        NotificationService.getInstance().markAllRead(userId);
    }

    /** Marks a single notification as read */
    public void markRead(Notification notification) {
        notification.setRead(true);
    }
}
