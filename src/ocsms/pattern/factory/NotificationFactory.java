package ocsms.pattern.factory;

import ocsms.model.Notification;
import ocsms.model.Notification.NotificationType;

/**
 * FACTORY PATTERN — NotificationFactory
 * Centralizes creation of Notification objects with type-specific messaging.
 * Used in: UC-20 (Notification System)
 */
public class NotificationFactory {

    /**
     * Creates a typed Notification for a specific user.
     *
     * @param type      The category of notification (membership, event, etc.)
     * @param message   The human-readable notification message
     * @param userId    The recipient user ID
     * @return          A ready-to-dispatch Notification object
     */
    public static Notification create(NotificationType type, String message, String userId) {
        return new Notification(userId, message, type);
    }

    // ── Convenience Factory Methods ─────────────────────────────────────────────

    public static Notification membershipApproved(String userId, String societyName) {
        return create(NotificationType.MEMBERSHIP_APPROVED,
                "🎉 Your application to " + societyName + " has been approved! Welcome aboard.",
                userId);
    }

    public static Notification membershipRejected(String userId, String societyName, String remarks) {
        return create(NotificationType.MEMBERSHIP_REJECTED,
                "❌ Your application to " + societyName + " was not accepted. Reason: " + remarks,
                userId);
    }

    public static Notification membershipApplied(String adminId, String studentName, String societyName) {
        return create(NotificationType.MEMBERSHIP_APPLIED,
                "📋 New membership application from " + studentName + " for " + societyName + ".",
                adminId);
    }

    public static Notification eventCreated(String userId, String eventTitle, String societyName) {
        return create(NotificationType.EVENT_CREATED,
                "📅 New event: " + eventTitle + " by " + societyName + ". Check it out!",
                userId);
    }

    public static Notification eventCancelled(String userId, String eventTitle) {
        return create(NotificationType.EVENT_CANCELLED,
                "⚠️ Event cancelled: " + eventTitle + ". We apologize for the inconvenience.",
                userId);
    }

    public static Notification eventRegistered(String userId, String eventTitle) {
        return create(NotificationType.EVENT_REGISTERED,
                "✅ You are registered for: " + eventTitle + ". See you there!",
                userId);
    }

    public static Notification bookingConfirmed(String userId, String resource, String dateTime) {
        return create(NotificationType.BOOKING_CONFIRMED,
                "📌 Booking confirmed: " + resource + " on " + dateTime + ".",
                userId);
    }

    public static Notification certificateReady(String userId, String eventTitle) {
        return create(NotificationType.CERTIFICATE_READY,
                "🏆 Your certificate for " + eventTitle + " is ready for download!",
                userId);
    }

    public static Notification voteResult(String userId, String societyName, String position, String winner) {
        return create(NotificationType.VOTE_RESULT,
                "🗳️ Election results for " + societyName + " — " + position + ": Winner is " + winner + "!",
                userId);
    }

    public static Notification announcement(String userId, String title, String societyName) {
        return create(NotificationType.ANNOUNCEMENT,
                "📢 [" + societyName + "] " + title,
                userId);
    }
}
