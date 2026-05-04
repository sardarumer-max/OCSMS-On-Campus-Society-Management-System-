package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Notification
 * In-app notification sent to a user triggered by system events.
 */
import java.io.Serializable;
public class Notification implements Serializable {

    public enum NotificationType {
        MEMBERSHIP_APPROVED, MEMBERSHIP_REJECTED, MEMBERSHIP_APPLIED,
        EVENT_CREATED, EVENT_CANCELLED, EVENT_REGISTERED,
        ANNOUNCEMENT, VOTE_RESULT, BOOKING_CONFIRMED,
        CERTIFICATE_READY, GENERAL
    }

    private String id;
    private String userId;           // recipient
    private String message;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    private boolean isRead;

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String userId, String message, NotificationType notificationType) {
        this();
        this.userId = userId;
        this.message = message;
        this.notificationType = notificationType;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
