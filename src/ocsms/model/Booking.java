package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Booking
 * Represents a resource booking (venue, equipment, etc.) by a society.
 */
import java.io.Serializable;
public class Booking implements Serializable {

    public enum BookingStatus { PENDING, CONFIRMED, CANCELLED }
    public enum ResourceType { AUDITORIUM, SEMINAR_HALL, LAB, SPORTS_GROUND, CONFERENCE_ROOM, OTHER }

    private String id;
    private ResourceType resourceType;
    private String resourceName;
    private String societyId;
    private LocalDateTime dateTime;
    private double durationHours;
    private BookingStatus status;

    public Booking() {
        this.id = UUID.randomUUID().toString();
        this.status = BookingStatus.CONFIRMED;
    }

    public Booking(ResourceType resourceType, String resourceName, String societyId,
                   LocalDateTime dateTime, double durationHours) {
        this();
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.societyId = societyId;
        this.dateTime = dateTime;
        this.durationHours = durationHours;
    }

    /** Returns the end time of this booking */
    public LocalDateTime getEndTime() {
        if (dateTime == null) return null;
        return dateTime.plusMinutes((long)(durationHours * 60));
    }

    /** Checks if this booking overlaps with a given time range for the same resource */
    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        if (status == BookingStatus.CANCELLED) return false;
        return !(end.isBefore(dateTime) || start.isAfter(getEndTime()));
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public double getDurationHours() { return durationHours; }
    public void setDurationHours(double durationHours) { this.durationHours = durationHours; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
