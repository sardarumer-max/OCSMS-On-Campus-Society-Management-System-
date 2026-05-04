package ocsms.util;

import ocsms.model.User;

/**
 * SINGLETON PATTERN — SessionManager
 * Holds the currently logged-in user for the entire application session.
 * Only one session can exist at a time (Singleton).
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private long lastActivityTime;
    private static final long IDLE_TIMEOUT_MS = 30 * 60 * 1000L; // 30 minutes

    // SINGLETON: private constructor prevents external instantiation
    private SessionManager() {}

    /** SINGLETON: returns the single global instance */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /** Start a new session for the authenticated user */
    public void login(User user) {
        this.currentUser = user;
        this.lastActivityTime = System.currentTimeMillis();
    }

    /** End the current session */
    public void logout() {
        this.currentUser = null;
    }

    /** Returns the currently logged-in user, or null if not logged in */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Update the last activity timestamp (call on any user action) */
    public void updateActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    /** Returns true if the session has been idle for 30+ minutes */
    public boolean isIdleTimeout() {
        if (currentUser == null) return false;
        return (System.currentTimeMillis() - lastActivityTime) >= IDLE_TIMEOUT_MS;
    }

    /** Returns true if a user is currently logged in */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Convenience: check if current user has a specific role */
    public boolean hasRole(User.UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
}
