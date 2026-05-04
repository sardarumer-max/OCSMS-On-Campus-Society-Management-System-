package ocsms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MODEL CLASS — Society
 * Represents a student society on campus.
 */
import java.io.Serializable;
public class Society implements Serializable {

    public enum SocietyStatus { ACTIVE, ARCHIVED }

    public enum SocietyCategory {
        TECHNOLOGY, ARTS, SPORTS, SCIENCE, LITERARY, SOCIAL, OTHER
    }

    private String id;
    private String name;
    private SocietyCategory category;
    private String description;
    private String advisorId;      // User ID of Faculty Advisor
    private int capacity;
    private SocietyStatus status;
    private List<String> memberIds; // User IDs of approved members

    public Society() {
        this.id = UUID.randomUUID().toString();
        this.status = SocietyStatus.ACTIVE;
        this.memberIds = new ArrayList<>();
    }

    public Society(String name, SocietyCategory category, String description, String advisorId, int capacity) {
        this();
        this.name = name;
        this.category = category;
        this.description = description;
        this.advisorId = advisorId;
        this.capacity = capacity;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SocietyCategory getCategory() { return category; }
    public void setCategory(SocietyCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdvisorId() { return advisorId; }
    public void setAdvisorId(String advisorId) { this.advisorId = advisorId; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public SocietyStatus getStatus() { return status; }
    public void setStatus(SocietyStatus status) { this.status = status; }

    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) memberIds.add(userId);
    }

    public void removeMember(String userId) { memberIds.remove(userId); }

    public boolean isFull() { return memberIds.size() >= capacity; }

    @Override
    public String toString() { return name; }
}
