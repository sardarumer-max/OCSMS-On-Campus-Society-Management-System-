package ocsms.controller;

import ocsms.model.User;
import ocsms.pattern.factory.UserFactory;
import ocsms.service.AuthService;
import ocsms.service.AuthService.LoginResult;
import ocsms.util.DataStore;
import ocsms.util.ValidationUtil;
import ocsms.util.SupabaseClient;
import ocsms.util.SupabaseConfig;

/**
 * CONTROLLER (MVC) — AuthController
 * Handles login and registration logic. Called by LoginFrame (View).
 * No Swing code here — only business logic.
 */
public class AuthController {

    private final AuthService authService = new AuthService();

    /** Result object for registration */
    public static class RegisterResult {
        public final boolean success;
        public final String message;
        public RegisterResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * Delegates login to AuthService and returns the result.
     */
    public LoginResult login(String rollNumber, String password) {
        return authService.login(rollNumber, password);
    }

    /**
     * Registers a new user after validating all fields.
     * UC-01: validates roll number format, password rules, duplicate check.
     */
    public RegisterResult register(String rollNumber, String name, String email,
                                    String password, String confirmPassword,
                                    User.UserRole role) {
        // Validate roll number
        if (!ValidationUtil.isValidRollNumber(rollNumber)) {
            return new RegisterResult(false,
                    "Invalid Roll Number format. Expected format: 24P-0557");
        }

        // Validate name
        if (!ValidationUtil.isNotBlank(name)) {
            return new RegisterResult(false, "Name cannot be empty.");
        }

        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
            return new RegisterResult(false, "Please enter a valid email address.");
        }

        // Validate password
        if (!ValidationUtil.isValidPassword(password)) {
            return new RegisterResult(false,
                    "Password must be at least 8 characters and contain at least 1 digit.");
        }

        // Confirm password match
        if (!password.equals(confirmPassword)) {
            return new RegisterResult(false, "Passwords do not match.");
        }

        // Check for duplicate roll number
        if (DataStore.getInstance().findUserByRollNumber(rollNumber) != null) {
            return new RegisterResult(false, "An account with this Roll Number already exists.");
        }

        // Create user via Factory Pattern
        User newUser = UserFactory.createUser(role, rollNumber, name, email, password);
        
        if (SupabaseConfig.isConfigured()) {
            boolean success = SupabaseClient.insert("users", newUser);
            if (!success) {
                return new RegisterResult(false, "Failed to create account in Supabase Database.");
            }
        }
        
        // Add to local cache
        DataStore.getInstance().getUsers().add(newUser);

        return new RegisterResult(true, "Account created successfully! You can now log in.");
    }

    /** Logs out the current session */
    public void logout() {
        authService.logout();
    }
}
