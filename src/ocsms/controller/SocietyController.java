package ocsms.controller;

import ocsms.model.Society;
import ocsms.model.Society.SocietyCategory;
import ocsms.model.Society.SocietyStatus;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.SessionManager;
import ocsms.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER (MVC) — SocietyController
 * Handles society creation, archiving, and profile queries.
 * Used in: UC-05 (Creation), UC-11 (Profile), UC-18 (Deactivation)
 */
public class SocietyController {

    /**
     * Creates a new society (Faculty Advisor or University Admin only).
     * UC-05: validates no duplicate name.
     */
    public String createSociety(String name, SocietyCategory category, String description,
                                 String advisorId, int capacity) {
        if (!ValidationUtil.isNotBlank(name))
            return "Society name cannot be empty.";
        if (!ValidationUtil.isNotBlank(description))
            return "Description cannot be empty.";
        if (!ValidationUtil.isValidCapacity(capacity))
            return "Capacity must be a positive number.";

        // Duplicate name check
        boolean duplicate = DataStore.getInstance().getSocieties().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name.trim()));
        if (duplicate)
            return "A society with this name already exists.";

        Society society = new Society(name.trim(), category, description.trim(), advisorId, capacity);
        DataStore.getInstance().getSocieties().add(society);
        return null; // null = success
    }

    /**
     * Archives (deactivates) a society — University Admin only.
     * UC-18: cancels all events, notifies all members.
     */
    public String archiveSociety(String societyId, String reason) {
        Society society = DataStore.getInstance().findSocietyById(societyId);
        if (society == null) return "Society not found.";
        if (society.getStatus() == SocietyStatus.ARCHIVED) return "Society is already archived.";

        // Mark as archived
        society.setStatus(SocietyStatus.ARCHIVED);

        // Cancel all upcoming events for this society
        DataStore.getInstance().getEvents().stream()
                .filter(e -> e.getSocietyId().equals(societyId))
                .filter(e -> e.getStatus() != ocsms.model.Event.EventStatus.PAST)
                .forEach(e -> e.setStatus(ocsms.model.Event.EventStatus.CANCELLED));

        // Notify all members
        for (String memberId : society.getMemberIds()) {
            NotificationService.getInstance().dispatch(
                    NotificationFactory.create(
                            ocsms.model.Notification.NotificationType.GENERAL,
                            "⚠️ Society \"" + society.getName() + "\" has been archived. Reason: " + reason,
                            memberId));
        }

        return null; // success
    }

    /**
     * Returns all societies, optionally filtered by category and status.
     * UC-13: search and discovery.
     */
    public List<Society> searchSocieties(String keyword, SocietyCategory category, boolean activeOnly) {
        List<Society> result = new ArrayList<>();
        for (Society s : DataStore.getInstance().getSocieties()) {
            if (activeOnly && s.getStatus() != SocietyStatus.ACTIVE) continue;
            if (category != null && s.getCategory() != category) continue;
            if (keyword != null && !keyword.isBlank()) {
                String kw = keyword.toLowerCase();
                if (!s.getName().toLowerCase().contains(kw) &&
                    !s.getDescription().toLowerCase().contains(kw)) continue;
            }
            result.add(s);
        }
        return result;
    }

    /** Returns all active societies */
    public List<Society> getAllActiveSocieties() {
        return searchSocieties(null, null, true);
    }

    /** Returns all societies (including archived) */
    public List<Society> getAllSocieties() {
        return DataStore.getInstance().getSocieties();
    }

    /** Returns the society managed by the current Society Admin */
    public Society getAdminSociety() {
        String userId = SessionManager.getInstance().getCurrentUser().getId();
        return DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getMemberIds().contains(userId) || s.getAdvisorId().equals(userId))
                .filter(s -> DataStore.getInstance().getUsers().stream()
                        .anyMatch(u -> u.getId().equals(userId) &&
                                  u.getRole() == ocsms.model.User.UserRole.SOCIETY_ADMIN &&
                                  s.getMemberIds().contains(userId)))
                .findFirst()
                .orElse(DataStore.getInstance().getSocieties().stream()
                        .filter(s -> s.getMemberIds().contains(userId))
                        .findFirst().orElse(null));
    }

    /** Gets admin's society by checking who is a SOCIETY_ADMIN member */
    public Society getSocietyForAdmin(String adminId) {
        return DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getMemberIds().contains(adminId))
                .findFirst().orElse(null);
    }
}
