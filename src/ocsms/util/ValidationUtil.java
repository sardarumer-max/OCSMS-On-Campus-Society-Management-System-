package ocsms.util;

import java.util.regex.Pattern;

/**
 * UTILITY CLASS — ValidationUtil
 * Centralized validation methods for all user input.
 * All rules from the assignment specification are implemented here.
 */
public class ValidationUtil {

    // Roll Number: must match \d{2}[A-Z]-\d{4}  e.g. 24P-0557
    private static final Pattern ROLL_NUMBER_PATTERN = Pattern.compile("\\d{2}[A-Za-z]-\\d{4}");

    /**
     * Validates roll number format (e.g. 24P-0557)
     */
    public static boolean isValidRollNumber(String rollNumber) {
        if (rollNumber == null) return false;
        return ROLL_NUMBER_PATTERN.matcher(rollNumber.trim()).matches();
    }

    /**
     * Password: minimum 8 characters, at least 1 digit
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        return password.chars().anyMatch(Character::isDigit);
    }

    /**
     * Motivation statement: 50 to 500 characters
     */
    public static boolean isValidMotivation(String text) {
        if (text == null) return false;
        int len = text.trim().length();
        return len >= 50 && len <= 500;
    }

    /**
     * Announcement body: maximum 2000 characters
     */
    public static boolean isValidAnnouncementBody(String text) {
        if (text == null) return false;
        return text.length() <= 2000;
    }

    /**
     * Budget amount: positive decimal, reject negative
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    /**
     * Event capacity: positive integer only
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0;
    }

    /**
     * Poster file: .jpg or .png only
     */
    public static boolean isValidPosterFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".png");
    }

    /**
     * Receipt file: .pdf or .jpg only
     */
    public static boolean isValidReceiptFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".jpg");
    }

    /**
     * Checks if a string is non-null and non-blank
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Basic email format check
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.contains("@") && email.contains(".");
    }

    /**
     * File size check: max 5MB
     */
    public static boolean isFileSizeValid(long fileSizeBytes) {
        return fileSizeBytes <= 5L * 1024 * 1024; // 5MB
    }
}
