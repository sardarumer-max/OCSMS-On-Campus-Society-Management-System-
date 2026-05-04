package ocsms.controller;

import ocsms.model.Feedback;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — FeedbackController
 * Handles event feedback submission and aggregated ratings.
 * Used in: UC-12
 */
public class FeedbackController {

    /**
     * Submits feedback for an event.
     * UC-12: window closes 7 days post-event. Rating must be 1-5.
     */
    public String submitFeedback(String eventId, String memberId,
                                  int rating, String comment, boolean isAnonymous) {

        ocsms.model.Event event = DataStore.getInstance().findEventById(eventId);
        if (event == null) return "Event not found.";

        // Feedback window: 7 days post-event
        if (!DateUtil.isFeedbackOpen(event.getDateTime())) {
            return "Feedback period has ended (window closes 7 days after the event).";
        }

        // Rating validation
        if (rating < 1 || rating > 5) return "Rating must be between 1 and 5.";

        // Duplicate submission check
        boolean alreadySubmitted = DataStore.getInstance().getFeedbacks().stream()
                .anyMatch(f -> f.getEventId().equals(eventId) && f.getMemberId().equals(memberId));
        if (alreadySubmitted) return "You have already submitted feedback for this event.";

        Feedback feedback = new Feedback(eventId, memberId, rating,
                comment != null ? comment.trim() : "", isAnonymous);
        DataStore.getInstance().getFeedbacks().add(feedback);
        return null; // success
    }

    /** Returns all feedback for an event */
    public List<Feedback> getFeedbackForEvent(String eventId) {
        return DataStore.getInstance().getFeedbacks().stream()
                .filter(f -> f.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    /** Calculates average rating for an event (0.0 if no feedback yet) */
    public double getAverageRating(String eventId) {
        List<Feedback> list = getFeedbackForEvent(eventId);
        if (list.isEmpty()) return 0.0;
        return list.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
    }

    /** Returns rating distribution: index 0 = count of 1-star, index 4 = count of 5-star */
    public int[] getRatingDistribution(String eventId) {
        int[] dist = new int[5];
        getFeedbackForEvent(eventId).forEach(f -> dist[f.getRating() - 1]++);
        return dist;
    }
}
