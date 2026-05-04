package ocsms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UTILITY CLASS — DateUtil
 * Centralized date formatting and comparison helpers.
 */
public class DateUtil {

    public static final DateTimeFormatter DATE_FORMAT     = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    public static final DateTimeFormatter TIME_FORMAT     = DateTimeFormatter.ofPattern("hh:mm a");

    public static String format(LocalDateTime dt) {
        if (dt == null) return "—";
        return dt.format(DATETIME_FORMAT);
    }

    public static String format(LocalDate d) {
        if (d == null) return "—";
        return d.format(DATE_FORMAT);
    }

    public static String formatTime(LocalDateTime dt) {
        if (dt == null) return "—";
        return dt.format(TIME_FORMAT);
    }

    /** Returns true if the given date is at least 1 full day in the future */
    public static boolean isAtLeastOneDayInFuture(LocalDateTime dt) {
        if (dt == null) return false;
        return dt.isAfter(LocalDateTime.now().plusDays(1));
    }

    /** Returns true if the given time is at least 24 hours from now (for bookings) */
    public static boolean isAtLeast24HoursFromNow(LocalDateTime dt) {
        if (dt == null) return false;
        return dt.isAfter(LocalDateTime.now().plusHours(24));
    }

    /** Returns true if a 48-hour late-entry threshold has been exceeded */
    public static boolean isLateEntry(LocalDateTime eventDateTime) {
        if (eventDateTime == null) return false;
        return LocalDateTime.now().isAfter(eventDateTime.plusHours(48));
    }

    /** Returns true if feedback window (7 days post-event) is still open */
    public static boolean isFeedbackOpen(LocalDateTime eventDateTime) {
        if (eventDateTime == null) return false;
        return LocalDateTime.now().isBefore(eventDateTime.plusDays(7));
    }
}
