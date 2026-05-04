package ocsms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MODEL CLASS — Event
 * Represents a campus event organized by a society.
 */
import java.io.Serializable;
public class Event implements Serializable {

    public enum EventType { WORKSHOP, SEMINAR, COMPETITION, SOCIAL, SPORTS, CULTURAL, OTHER }
    public enum EventStatus { UPCOMING, ONGOING, PAST, CANCELLED }

    private String id;
    private String title;
    private LocalDateTime dateTime;
    private String venue;
    private int capacity;
    private EventType eventType;
    private String posterPath;
    private String societyId;
    private List<String> registeredMemberIds;
    private List<String> coSocietyIds; // for joint events
    private EventStatus status;
    private boolean isJointEvent;

    public Event() {
        this.id = UUID.randomUUID().toString();
        this.registeredMemberIds = new ArrayList<>();
        this.coSocietyIds = new ArrayList<>();
        this.status = EventStatus.UPCOMING;
        this.isJointEvent = false;
    }

    public Event(String title, LocalDateTime dateTime, String venue, int capacity,
                 EventType eventType, String societyId) {
        this();
        this.title = title;
        this.dateTime = dateTime;
        this.venue = venue;
        this.capacity = capacity;
        this.eventType = eventType;
        this.societyId = societyId;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public List<String> getRegisteredMemberIds() { return registeredMemberIds; }
    public void setRegisteredMemberIds(List<String> registeredMemberIds) { this.registeredMemberIds = registeredMemberIds; }

    public List<String> getCoSocietyIds() { return coSocietyIds; }
    public void setCoSocietyIds(List<String> coSocietyIds) { this.coSocietyIds = coSocietyIds; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public boolean isJointEvent() { return isJointEvent; }
    public void setJointEvent(boolean jointEvent) { isJointEvent = jointEvent; }

    public boolean isFull() { return registeredMemberIds.size() >= capacity; }

    public boolean isPast() { return dateTime != null && dateTime.isBefore(LocalDateTime.now()); }

    @Override
    public String toString() { return title; }
}
