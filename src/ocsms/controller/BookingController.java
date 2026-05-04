package ocsms.controller;

import ocsms.model.Booking;
import ocsms.model.Booking.BookingStatus;
import ocsms.model.Booking.ResourceType;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — BookingController
 * Handles resource booking with conflict detection.
 * Used in: UC-14
 */
public class BookingController {

    /**
     * Creates a booking after conflict check.
     * UC-14: booking must be at least 24 hours in advance.
     *         Detects conflicts with existing bookings for same resource.
     */
    public String createBooking(ResourceType type, String resourceName, String societyId,
                                 LocalDateTime dateTime, double durationHours) {

        if (resourceName == null || resourceName.isBlank()) return "Resource name is required.";
        if (durationHours <= 0) return "Duration must be a positive number.";

        // Must be at least 24 hours in advance
        if (!DateUtil.isAtLeast24HoursFromNow(dateTime)) {
            return "Bookings must be made at least 24 hours in advance.";
        }

        LocalDateTime endTime = dateTime.plusMinutes((long)(durationHours * 60));

        // Conflict check
        for (Booking existing : DataStore.getInstance().getBookings()) {
            if (existing.getStatus() == BookingStatus.CANCELLED) continue;
            if (existing.getResourceName() != null &&
                    existing.getResourceName().equalsIgnoreCase(resourceName.trim())) {
                if (existing.overlapsWith(dateTime, endTime)) {
                    LocalDateTime nextAvail = existing.getEndTime().plusMinutes(30);
                    return "Conflict detected: \"" + resourceName + "\" is already booked during that slot.\n" +
                           "Nearest available slot suggestion: " + DateUtil.format(nextAvail);
                }
            }
        }

        Booking booking = new Booking(type, resourceName.trim(), societyId, dateTime, durationHours);
        DataStore.getInstance().getBookings().add(booking);

        // Notify booking requester (society admin)
        DataStore.getInstance().getSocieties().stream()
                .filter(s -> s.getId().equals(societyId))
                .findFirst().ifPresent(society ->
                        society.getMemberIds().forEach(memberId -> {
                            if (DataStore.getInstance().findUserById(memberId) != null &&
                                DataStore.getInstance().findUserById(memberId).getRole()
                                        == ocsms.model.User.UserRole.SOCIETY_ADMIN) {
                                NotificationService.getInstance().dispatch(
                                        NotificationFactory.bookingConfirmed(memberId,
                                                resourceName.trim(), DateUtil.format(dateTime)));
                            }
                        }));

        return null; // success
    }

    /** Cancels an existing booking */
    public String cancelBooking(String bookingId) {
        Booking booking = DataStore.getInstance().getBookings().stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst().orElse(null);
        if (booking == null) return "Booking not found.";
        booking.setStatus(BookingStatus.CANCELLED);
        return null;
    }

    /** Returns all bookings for a society */
    public List<Booking> getBookingsForSociety(String societyId) {
        return DataStore.getInstance().getBookings().stream()
                .filter(b -> b.getSocietyId().equals(societyId))
                .collect(Collectors.toList());
    }

    /** Returns all active bookings */
    public List<Booking> getAllBookings() {
        return DataStore.getInstance().getBookings();
    }
}
