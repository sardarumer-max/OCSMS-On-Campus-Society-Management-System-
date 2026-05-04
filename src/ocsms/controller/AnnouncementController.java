package ocsms.controller;

import ocsms.model.Announcement;
import ocsms.model.Society;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — AnnouncementController
 * Handles composing, drafting, and publishing announcements.
 * Used in: UC-08
 */
public class AnnouncementController {

    /**
     * Saves an announcement as draft or publishes it.
     * UC-08: body max 2000 chars. Publishing notifies all society members.
     */
    public String saveAnnouncement(String societyId, String title, String body, boolean isDraft) {
        if (!ValidationUtil.isNotBlank(title)) return "Title cannot be empty.";
        if (!ValidationUtil.isNotBlank(body))  return "Body cannot be empty.";
        if (!ValidationUtil.isValidAnnouncementBody(body))
            return "Announcement body exceeds the 2000-character limit.";

        Announcement ann = new Announcement(title.trim(), body.trim(), societyId, isDraft);
        if (!isDraft) ann.setPublishedAt(LocalDateTime.now());
        DataStore.getInstance().getAnnouncements().add(ann);

        // Notify all society members only when publishing (not draft)
        if (!isDraft) {
            Society society = DataStore.getInstance().findSocietyById(societyId);
            if (society != null) {
                for (String memberId : society.getMemberIds()) {
                    NotificationService.getInstance().dispatch(
                            NotificationFactory.announcement(memberId, title.trim(), society.getName()));
                }
            }
        }

        return null; // success
    }

    /** Publishes a saved draft — updates status and notifies members */
    public String publishDraft(String announcementId) {
        Announcement ann = DataStore.getInstance().getAnnouncements().stream()
                .filter(a -> a.getId().equals(announcementId))
                .findFirst().orElse(null);
        if (ann == null) return "Announcement not found.";
        if (!ann.isDraft()) return "Announcement is already published.";

        ann.setDraft(false);
        ann.setPublishedAt(LocalDateTime.now());

        Society society = DataStore.getInstance().findSocietyById(ann.getSocietyId());
        if (society != null) {
            for (String memberId : society.getMemberIds()) {
                NotificationService.getInstance().dispatch(
                        NotificationFactory.announcement(memberId, ann.getTitle(), society.getName()));
            }
        }
        return null;
    }

    /** Returns all published announcements for a society */
    public List<Announcement> getPublishedForSociety(String societyId) {
        return DataStore.getInstance().getAnnouncements().stream()
                .filter(a -> a.getSocietyId().equals(societyId) && !a.isDraft())
                .sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()))
                .collect(Collectors.toList());
    }

    /** Returns all announcements (including drafts) for admin view */
    public List<Announcement> getAllForSociety(String societyId) {
        return DataStore.getInstance().getAnnouncements().stream()
                .filter(a -> a.getSocietyId().equals(societyId))
                .collect(Collectors.toList());
    }

    /** Returns all published announcements across all societies */
    public List<Announcement> getAllPublished() {
        return DataStore.getInstance().getAnnouncements().stream()
                .filter(a -> !a.isDraft())
                .sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()))
                .collect(Collectors.toList());
    }
}
