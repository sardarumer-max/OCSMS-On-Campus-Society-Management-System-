package ocsms.service;

import ocsms.model.Notification;
import ocsms.pattern.observer.NotificationListener;
import ocsms.util.DataStore;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN — NotificationService (Subject / Observable)
 * The central hub that dispatches notifications to all registered observers.
 * Any UI panel implements NotificationListener to auto-update on events.
 * Used in: UC-20, UC-03, UC-04, UC-06, UC-07, UC-08, UC-15
 */
public class NotificationService {

    private static NotificationService instance;

    // OBSERVER: list of registered UI listeners
    private final List<NotificationListener> listeners = new ArrayList<>();

    // SINGLETON: private constructor
    private NotificationService() {}

    /** SINGLETON: returns the single global instance */
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    // ── Observer Registration ───────────────────────────────────────────────────

    /** Register a UI panel to receive notification updates */
    public void addListener(NotificationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Unregister a panel (call on panel dispose) */
    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    // ── Dispatch ────────────────────────────────────────────────────────────────

    /**
     * Save notification to DataStore AND notify all registered UI listeners.
     * Also simulates email by writing to a log file.
     */
    public void dispatch(Notification notification) {
        // Save to persistent store
        DataStore.getInstance().getNotifications().add(notification);

        // OBSERVER: notify all registered listeners
        for (NotificationListener listener : listeners) {
            listener.onNotification(notification);
        }

        // Simulate email: write to log file
        logEmailNotification(notification);
    }

    /** Dispatch a list of notifications (bulk send) */
    public void dispatchAll(List<Notification> notifications) {
        notifications.forEach(this::dispatch);
    }

    // ── Query Helpers ───────────────────────────────────────────────────────────

    /** Returns all notifications for a specific user */
    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : DataStore.getInstance().getNotifications()) {
            if (n.getUserId().equals(userId)) result.add(n);
        }
        return result;
    }

    /** Returns count of unread notifications for a user */
    public int getUnreadCount(String userId) {
        return (int) DataStore.getInstance().getNotifications().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }

    /** Marks all notifications for a user as read */
    public void markAllRead(String userId) {
        DataStore.getInstance().getNotifications().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .forEach(n -> n.setRead(true));
    }

    // ── Email Simulation ────────────────────────────────────────────────────────

    /** Simulates email notification by appending to an email.log file */
    private void logEmailNotification(Notification n) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("email.log", true))) {
            writer.printf("[%s] TO:%s | TYPE:%s | %s%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    n.getUserId(),
                    n.getNotificationType(),
                    n.getMessage());
        } catch (IOException ignored) {
            // Email log is non-critical; silently ignore write failures
        }
    }
}
