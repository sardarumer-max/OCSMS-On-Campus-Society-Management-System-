package ocsms.model;

import java.time.LocalDateTime;

/**
 * MODEL CLASS — AttendanceRecord
 * Records attendance for a specific member at a specific event.
 */
import java.io.Serializable;
public class AttendanceRecord implements Serializable {

    public enum AttendanceStatus { PRESENT, ABSENT }

    private String eventId;
    private String memberId;
    private AttendanceStatus status;
    private LocalDateTime markedAt;
    private boolean isLateEntry; // flagged if marked 48+ hours after event

    public AttendanceRecord() {}

    public AttendanceRecord(String eventId, String memberId, AttendanceStatus status) {
        this.eventId = eventId;
        this.memberId = memberId;
        this.status = status;
        this.markedAt = LocalDateTime.now();
        this.isLateEntry = false;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public boolean isLateEntry() { return isLateEntry; }
    public void setLateEntry(boolean lateEntry) { isLateEntry = lateEntry; }
}
