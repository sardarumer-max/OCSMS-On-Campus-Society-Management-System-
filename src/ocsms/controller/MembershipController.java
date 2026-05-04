package ocsms.controller;

import ocsms.model.Membership;
import ocsms.model.Membership.MembershipStatus;
import ocsms.model.Society;
import ocsms.model.User;
import ocsms.pattern.factory.NotificationFactory;
import ocsms.service.NotificationService;
import ocsms.util.DataStore;
import ocsms.util.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER (MVC) — MembershipController
 * Handles membership application, approval, and rejection.
 * Used in: UC-03 (Apply), UC-04 (Approve/Reject)
 */
public class MembershipController {

    /**
     * Student applies to join a society.
     * UC-03: validates not already member, not pending, society not full.
     */
    public String applyForMembership(String studentId, String societyId, String motivation) {
        // Validate motivation statement
        if (!ValidationUtil.isValidMotivation(motivation)) {
            return "Motivation statement must be between 50 and 500 characters. " +
                   "Current: " + (motivation == null ? 0 : motivation.trim().length()) + " characters.";
        }

        Society society = DataStore.getInstance().findSocietyById(societyId);
        if (society == null) return "Society not found.";

        // Check if already a member
        if (society.getMemberIds().contains(studentId)) {
            return "You are already a member of this society.";
        }

        // Check if society is full
        if (society.isFull()) {
            return "This society has reached its member limit (" + society.getCapacity() + " members).";
        }

        // Check for existing pending application
        boolean alreadyApplied = DataStore.getInstance().getMemberships().stream()
                .anyMatch(m -> m.getStudentId().equals(studentId)
                        && m.getSocietyId().equals(societyId)
                        && m.getStatus() == MembershipStatus.PENDING);
        if (alreadyApplied) {
            return "Your application to this society is already under review.";
        }

        // Create application
        Membership membership = new Membership(studentId, societyId, motivation.trim());
        DataStore.getInstance().getMemberships().add(membership);

        // Notify society admin(s)
        User student = DataStore.getInstance().findUserById(studentId);
        for (User admin : DataStore.getInstance().getUsers()) {
            if (admin.getRole() == User.UserRole.SOCIETY_ADMIN) {
                Society adminSociety = DataStore.getInstance().getSocieties().stream()
                        .filter(s -> s.getId().equals(societyId) && s.getMemberIds().contains(admin.getId()))
                        .findFirst().orElse(null);
                if (adminSociety != null) {
                    NotificationService.getInstance().dispatch(
                            NotificationFactory.membershipApplied(admin.getId(),
                                    student != null ? student.getName() : "Unknown", society.getName()));
                }
            }
        }

        return null; // null = success
    }

    /**
     * Society Admin approves a membership application.
     * UC-04: changes status, adds member to society, notifies applicant.
     */
    public String approveMembership(String membershipId) {
        Membership m = findById(membershipId);
        if (m == null) return "Application not found.";
        if (m.getStatus() != MembershipStatus.PENDING) return "Application is no longer pending.";

        Society society = DataStore.getInstance().findSocietyById(m.getSocietyId());
        if (society == null) return "Society not found.";
        if (society.isFull()) return "Society is now full. Cannot approve.";

        m.setStatus(MembershipStatus.APPROVED);
        society.addMember(m.getStudentId());

        // Notify the student
        NotificationService.getInstance().dispatch(
                NotificationFactory.membershipApproved(m.getStudentId(), society.getName()));

        return null; // success
    }

    /**
     * Society Admin rejects a membership application with mandatory remarks.
     * UC-04: remarks are required on rejection.
     */
    public String rejectMembership(String membershipId, String remarks) {
        if (!ValidationUtil.isNotBlank(remarks)) {
            return "Rejection remarks are required. Please explain the reason.";
        }

        Membership m = findById(membershipId);
        if (m == null) return "Application not found.";
        if (m.getStatus() != MembershipStatus.PENDING) return "Application is no longer pending.";

        m.setStatus(MembershipStatus.REJECTED);
        m.setRemarks(remarks.trim());

        // Notify the student
        Society society = DataStore.getInstance().findSocietyById(m.getSocietyId());
        String societyName = society != null ? society.getName() : "the society";
        NotificationService.getInstance().dispatch(
                NotificationFactory.membershipRejected(m.getStudentId(), societyName, remarks.trim()));

        return null; // success
    }

    /** Returns all pending applications for a specific society */
    public List<Membership> getPendingApplications(String societyId) {
        return DataStore.getInstance().getMemberships().stream()
                .filter(m -> m.getSocietyId().equals(societyId) && m.getStatus() == MembershipStatus.PENDING)
                .collect(Collectors.toList());
    }

    /** Returns all memberships for a specific student */
    public List<Membership> getMembershipsForStudent(String studentId) {
        return DataStore.getInstance().getMemberships().stream()
                .filter(m -> m.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /** Returns all memberships across all societies */
    public List<Membership> getAllMemberships() {
        return DataStore.getInstance().getMemberships();
    }

    private Membership findById(String id) {
        return DataStore.getInstance().getMemberships().stream()
                .filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
}
