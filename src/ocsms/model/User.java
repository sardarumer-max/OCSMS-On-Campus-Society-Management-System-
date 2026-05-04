package ocsms.model;

import java.util.UUID;

/**
 * MODEL CLASS — User
 * Represents any user of the OCSMS system with role-based identity.
 */
import java.io.Serializable;
public class User implements Serializable {

    public enum UserRole {
        UNIVERSITY_ADMIN, SOCIETY_ADMIN, FACULTY_ADVISOR, TREASURER, MEMBER
    }

    private String id;
    private String rollNumber;
    private String name;
    private String email;
    private String passwordHash; // stored as plain text for demo purposes
    private UserRole role;
    private boolean isLocked;
    private int failedAttempts;
    private long lockTime; // epoch millis when locked

    public User() {
        this.id = UUID.randomUUID().toString();
        this.failedAttempts = 0;
        this.isLocked = false;
    }

    public User(String rollNumber, String name, String email, String passwordHash, UserRole role) {
        this();
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public long getLockTime() { return lockTime; }
    public void setLockTime(long lockTime) { this.lockTime = lockTime; }

    @Override
    public String toString() {
        return name + " (" + rollNumber + ") — " + role;
    }
}
