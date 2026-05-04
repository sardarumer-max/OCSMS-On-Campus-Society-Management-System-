package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Announcement
 * Society announcements / notices posted by Society Admin.
 */
import java.io.Serializable;
public class Announcement implements Serializable {

    private String id;
    private String title;
    private String body;      // max 2000 chars
    private String societyId;
    private boolean isDraft;
    private LocalDateTime publishedAt;

    public Announcement() {
        this.id = UUID.randomUUID().toString();
        this.isDraft = false;
    }

    public Announcement(String title, String body, String societyId, boolean isDraft) {
        this();
        this.title = title;
        this.body = body;
        this.societyId = societyId;
        this.isDraft = isDraft;
        if (!isDraft) this.publishedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { isDraft = draft; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
