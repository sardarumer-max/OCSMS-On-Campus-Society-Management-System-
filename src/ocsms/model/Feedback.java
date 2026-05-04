package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Feedback
 * Post-event member feedback with optional star rating and comment.
 */
import java.io.Serializable;
public class Feedback implements Serializable {

    private String id;
    private String eventId;
    private String memberId;
    private int rating;      // 1 to 5
    private String comment;  // optional
    private boolean isAnonymous;
    private LocalDateTime submittedAt;

    public Feedback() {
        this.id = UUID.randomUUID().toString();
        this.submittedAt = LocalDateTime.now();
        this.isAnonymous = true;
    }

    public Feedback(String eventId, String memberId, int rating, String comment, boolean isAnonymous) {
        this();
        this.eventId = eventId;
        this.memberId = memberId;
        this.rating = Math.max(1, Math.min(5, rating)); // clamp to 1-5
        this.comment = comment;
        this.isAnonymous = isAnonymous;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
