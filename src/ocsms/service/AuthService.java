package ocsms.service;

import ocsms.model.User;
import ocsms.util.DataStore;
import ocsms.util.SessionManager;
import ocsms.util.ValidationUtil;

/**
 * SERVICE CLASS — AuthService
 * Handles authentication logic: login, account locking, and validation.
 * Called by AuthController; no Swing code here (pure logic layer).
 */
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS    = 15 * 60 * 1000L; // 15 minutes

    /** Result object returned by login attempt */
    public static class LoginResult {
        public enum Status { SUCCESS, INVALID_CREDENTIALS, ACCOUNT_LOCKED, VALIDATION_ERROR }
        public final Status status;
        public final String message;
        public final User user;

        public LoginResult(Status status, String message, User user) {
            this.status = status;
            this.message = message;
            this.user = user;
        }
    }

    /**
     * Attempts to log in with the given credentials.
     * Handles: failed attempt counting, account locking, idle auto-unlock.
     */
    public LoginResult login(String rollNumber, String password) {
        // Basic input validation
        if (!ValidationUtil.isNotBlank(rollNumber) || !ValidationUtil.isNotBlank(password)) {
            return new LoginResult(LoginResult.Status.VALIDATION_ERROR,
                    "Roll Number and Password cannot be empty.", null);
        }

        User user = DataStore.getInstance().findUserByRollNumber(rollNumber.trim());

        if (user == null) {
            return new LoginResult(LoginResult.Status.INVALID_CREDENTIALS,
                    "No account found with that Roll Number.", null);
        }

        // Check if account is locked
        if (user.isLocked()) {
            long elapsed = System.currentTimeMillis() - user.getLockTime();
            if (elapsed < LOCK_DURATION_MS) {
                long minutesLeft = (LOCK_DURATION_MS - elapsed) / 60000;
                return new LoginResult(LoginResult.Status.ACCOUNT_LOCKED,
                        "Account is locked. Try again in " + minutesLeft + " minute(s).", null);
            } else {
                // Auto-unlock after 15 minutes
                user.setLocked(false);
                user.setFailedAttempts(0);
            }
        }

        // Verify password (plain-text comparison for demo)
        if (!user.getPasswordHash().equals(password)) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
                user.setLockTime(System.currentTimeMillis());
                return new LoginResult(LoginResult.Status.ACCOUNT_LOCKED,
                        "Too many failed attempts. Account locked for 15 minutes.", null);
            }
            int remaining = MAX_FAILED_ATTEMPTS - user.getFailedAttempts();
            return new LoginResult(LoginResult.Status.INVALID_CREDENTIALS,
                    "Incorrect password. " + remaining + " attempt(s) remaining.", null);
        }

        // Successful login
        user.setFailedAttempts(0);
        SessionManager.getInstance().login(user);
        return new LoginResult(LoginResult.Status.SUCCESS, "Login successful.", user);
    }

    /** Logs out the current user */
    public void logout() {
        SessionManager.getInstance().logout();
    }
}
