package ocsms.controller;

import ocsms.model.Booking;
import ocsms.model.Event;
import ocsms.model.Event.EventStatus;
import ocsms.model.Event.EventType;
import ocsms.model.Society;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;
import ocsms.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — EventController
 * Handles event creation, registration, cancellation, and joint events.
 * Used in: UC-06 (Create), UC-07 (Register), UC-19 (Joint Events)
 */
public class EventController {

    /**
     * Creates a new event after full validation.
     * UC-06: validates capacity, date, venue conflict.
     */
    public String createEvent(String title, LocalDateTime dateTime, String venue,
                               int capacity, EventType type, String societyId,
                               String posterPath, boolean isJoint, List<String> coSocietyIds) {

        if (!ValidationUtil.isNotBlank(title))       return "Event title cannot be empty.";
        if (!ValidationUtil.isNotBlank(venue))        return "Venue cannot be empty.";
        if (!ValidationUtil.isValidCapacity(capacity)) return "Capacity must be a positive number.";

        // Event date must be at least 1 day in the future
        if (!DateUtil.isAtLeastOneDayInFuture(dateTime)) {
            return "Event date must be at least 1 day in the future.";
        }

        // Poster validation (only if a path was provided)
        if (posterPath != null && !posterPath.isBlank()) {
            if (!ValidationUtil.isValidPosterFile(posterPath)) {
                return "Poster must be a .jpg or .png file.";
            }
        }

        // Venue conflict check against existing bookings
        String conflict = checkVenueConflict(venue, dateTime, dateTime.plusHours(2), null);
        if (conflict != null) return conflict;

        Event event = new Event(title.trim(), dateTime, venue.trim(), capacity, type, societyId);
        event.setJointEvent(isJoint);
        if (isJoint && coSocietyIds != null) event.getCoSocietyIds().addAll(coSocietyIds);
        if (posterPath != null && !posterPath.isBlank()) event.setPosterPath(posterPath);

        DataStore.getInstance().getEvents().add(event);

        // Automatically create a booking for the venue
        Booking booking = new Booking(Booking.ResourceType.AUDITORIUM, venue.trim(),
                societyId, dateTime, 2.0);
        DataStore.getInstance().getBookings().add(booking);

        // Notify all members of the primary society
        Society society = DataStore.getInstance().findSocietyById(societyId);
        if (society != null) {
            for (String memberId : society.getMemberIds()) {
                NotificationService.getInstance().dispatch(
                        NotificationFactory.eventCreated(memberId, title.trim(), society.getName()));
            }
            // Notify co-society members for joint events
            if (isJoint && coSocietyIds != null) {
                for (String coId : coSocietyIds) {
                    Society coSociety = DataStore.getInstance().findSocietyById(coId);
                    if (coSociety != null) {
                        for (String memberId : coSociety.getMemberIds()) {
                            NotificationService.getInstance().dispatch(
                                    NotificationFactory.eventCreated(memberId, title.trim(), society.getName()));
                        }
                    }
                }
            }
        }

        return null; // success
    }

    /**
     * Registers a member for an event.
     * UC-07: validates capacity, deadline, duplicate registration.
     */
    public String registerForEvent(String memberId, String eventId) {
        Event event = DataStore.getInstance().findEventById(eventId);
        if (event == null) return "Event not found.";

        if (event.getStatus() == EventStatus.CANCELLED) return "This event has been cancelled.";
        if (event.isPast()) return "Registration for this event is closed.";
        if (event.getRegisteredMemberIds().contains(memberId)) return "You are already registered for this event.";
        if (event.isFull()) return "This event has reached full capacity. You have been added to the waitlist concept — check back later.";

        event.getRegisteredMemberIds().add(memberId);

        // Notify the member
        NotificationService.getInstance().dispatch(
                NotificationFactory.eventRegistered(memberId, event.getTitle()));

        return null; // success
    }

    /**
     * Cancels an event. Society Admin only.
     * UC-06: cancels all registrations, notifies all registered members.
     */
    public String cancelEvent(String eventId) {
        Event event = DataStore.getInstance().findEventById(eventId);
        if (event == null) return "Event not found.";
        if (event.getStatus() == EventStatus.CANCELLED) return "Event is already cancelled.";

        event.setStatus(EventStatus.CANCELLED);

        // Notify all registered members
        for (String memberId : event.getRegisteredMemberIds()) {
            NotificationService.getInstance().dispatch(
                    NotificationFactory.eventCancelled(memberId, event.getTitle()));
        }

        return null; // success
    }

    /** Returns events for a specific society */
    public List<Event> getEventsForSociety(String societyId) {
        return DataStore.getInstance().getEvents().stream()
                .filter(e -> e.getSocietyId().equals(societyId) ||
                             e.getCoSocietyIds().contains(societyId))
                .collect(Collectors.toList());
    }

    /** Returns all upcoming (non-cancelled, non-past) events */
    public List<Event> getUpcomingEvents() {
        return DataStore.getInstance().getEvents().stream()
                .filter(e -> e.getStatus() == EventStatus.UPCOMING && !e.isPast())
                .collect(Collectors.toList());
    }

    /** Returns all events */
    public List<Event> getAllEvents() {
        return DataStore.getInstance().getEvents();
    }

    /** Returns events a member is registered for */
    public List<Event> getRegisteredEvents(String memberId) {
        return DataStore.getInstance().getEvents().stream()
                .filter(e -> e.getRegisteredMemberIds().contains(memberId))
                .collect(Collectors.toList());
    }

    /** Checks venue availability conflict for a given time range */
    private String checkVenueConflict(String venue, LocalDateTime start, LocalDateTime end, String excludeBookingId) {
        for (Booking b : DataStore.getInstance().getBookings()) {
            if (excludeBookingId != null && b.getId().equals(excludeBookingId)) continue;
            if (b.getResourceName() != null && b.getResourceName().equalsIgnoreCase(venue)) {
                if (b.overlapsWith(start, end)) {
                    return "Venue \"" + venue + "\" is already booked during this time. " +
                           "Please choose a different slot or venue.";
                }
            }
        }
        return null;
    }
}
