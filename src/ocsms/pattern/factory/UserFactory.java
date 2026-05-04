package ocsms.pattern.factory;

import ocsms.model.User;
import ocsms.model.User.UserRole;

import java.util.UUID;

/**
 * FACTORY PATTERN — UserFactory
 * Centralizes creation of User objects with role-specific setup.
 * Avoids duplicating role-specific logic across multiple registration flows.
 * Used in: UC-01 (Registration)
 */
public class UserFactory {

    /**
     * Creates a User object with role-specific initialization.
     * Password is stored as plain-text for demo purposes
     * (in production this would be a bcrypt hash).
     *
     * @param role        The role to assign to this user
     * @param rollNumber  FAST roll number (e.g. 24P-0557)
     * @param name        Full name
     * @param email       University email address
     * @param password    Raw password (stored as-is for demo)
     * @return            Fully constructed User object
     */
    public static User createUser(UserRole role, String rollNumber,
                                   String name, String email, String password) {
        User user = new User();
        user.setRollNumber(rollNumber.trim());
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(password); // plain-text for demo
        user.setRole(role);
        user.setLocked(false);
        user.setFailedAttempts(0);
        return user;
    }
}
