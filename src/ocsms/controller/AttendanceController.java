package ocsms.controller;

import ocsms.model.AttendanceRecord;
import ocsms.model.AttendanceRecord.AttendanceStatus;
import ocsms.model.Certificate;
import ocsms.model.Event;
import ocsms.model.User;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.DateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — AttendanceController (handles UC-10 + UC-17)
 * Manages attendance marking and auto-generates certificates for present members.
 */
public class AttendanceController {

    /**
     * Marks or updates attendance for a member at an event.
     * UC-10: flags late entries (48h+ after event), auto-generates certificate if PRESENT.
     */
    public void markAttendance(String eventId, String memberId, AttendanceStatus status) {
        Event event = DataStore.getInstance().findEventById(eventId);

        // Find existing record or create new one
        AttendanceRecord existing = DataStore.getInstance().getAttendanceRecords().stream()
                .filter(a -> a.getEventId().equals(eventId) && a.getMemberId().equals(memberId))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setStatus(status);
            existing.setMarkedAt(LocalDateTime.now());
            existing.setLateEntry(event != null && DateUtil.isLateEntry(event.getDateTime()));
        } else {
            AttendanceRecord record = new AttendanceRecord(eventId, memberId, status);
            if (event != null) record.setLateEntry(DateUtil.isLateEntry(event.getDateTime()));
            DataStore.getInstance().getAttendanceRecords().add(record);
        }

        // UC-17: auto-generate certificate if marked PRESENT
        if (status == AttendanceStatus.PRESENT && event != null) {
            boolean certExists = DataStore.getInstance().getCertificates().stream()
                    .anyMatch(c -> c.getMemberId().equals(memberId) && c.getEventId().equals(eventId));
            if (!certExists) {
                Certificate cert = new Certificate(memberId, eventId, event.getSocietyId());
                DataStore.getInstance().getCertificates().add(cert);

                // Notify the member
                NotificationService.getInstance().dispatch(
                        NotificationFactory.certificateReady(memberId, event.getTitle()));
            }
        }
    }

    /** Returns all attendance records for an event */
    public List<AttendanceRecord> getAttendanceForEvent(String eventId) {
        return DataStore.getInstance().getAttendanceRecords().stream()
                .filter(a -> a.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    /** Calculates attendance percentage for an event */
    public double getAttendancePercentage(String eventId) {
        List<AttendanceRecord> records = getAttendanceForEvent(eventId);
        if (records.isEmpty()) return 0.0;
        long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        return (present * 100.0) / records.size();
    }

    /** Returns certificates for a specific member */
    public List<Certificate> getCertificatesForMember(String memberId) {
        return DataStore.getInstance().getCertificates().stream()
                .filter(c -> c.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    /** Returns attendance status for a specific member at an event */
    public AttendanceStatus getStatus(String eventId, String memberId) {
        return DataStore.getInstance().getAttendanceRecords().stream()
                .filter(a -> a.getEventId().equals(eventId) && a.getMemberId().equals(memberId))
                .map(AttendanceRecord::getStatus)
                .findFirst().orElse(null);
    }
}
