package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Certificate
 * Auto-generated certificate of attendance for a member at an event.
 */
import java.io.Serializable;
public class Certificate implements Serializable {

    private String id;
    private String memberId;
    private String eventId;
    private String societyId;
    private String verificationCode;  // unique alphanumeric code
    private LocalDateTime generatedAt;
    private String filePath;           // path if saved to disk

    public Certificate() {
        this.id = UUID.randomUUID().toString();
        this.generatedAt = LocalDateTime.now();
        this.verificationCode = generateVerificationCode();
    }

    public Certificate(String memberId, String eventId, String societyId) {
        this();
        this.memberId = memberId;
        this.eventId = eventId;
        this.societyId = societyId;
    }

    /** Generates a short alphanumeric verification code */
    private String generateVerificationCode() {
        return "OCSMS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
