package ocsms.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MODEL CLASS — Membership
 * Tracks a student's application to join a society.
 */
import java.io.Serializable;
public class Membership implements Serializable {

    public enum MembershipStatus { PENDING, APPROVED, REJECTED }

    private String id;
    private String studentId;   // User ID
    private String societyId;   // Society ID
    private String motivationStatement;
    private MembershipStatus status;
    private String remarks;     // Rejection remarks (mandatory on rejection)
    private LocalDateTime appliedAt;

    public Membership() {
        this.id = UUID.randomUUID().toString();
        this.status = MembershipStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    public Membership(String studentId, String societyId, String motivationStatement) {
        this();
        this.studentId = studentId;
        this.societyId = societyId;
        this.motivationStatement = motivationStatement;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSocietyId() { return societyId; }
    public void setSocietyId(String societyId) { this.societyId = societyId; }

    public String getMotivationStatement() { return motivationStatement; }
    public void setMotivationStatement(String motivationStatement) { this.motivationStatement = motivationStatement; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
