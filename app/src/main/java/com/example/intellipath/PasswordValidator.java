package com.example.intellipath;

public final class PasswordValidator {

    public static final int MIN_LENGTH = 8;

    private PasswordValidator() {}

    // Returns an error message, or null if the password is valid.
    public static String getError(String value) {

        if (value.length() < MIN_LENGTH) {
            return "Password must be at least " + MIN_LENGTH + " characters.";
        }
        if (!value.matches(".*[A-Z].*")) {
            return "Password must contain an uppercase letter.";
        }
        if (!value.matches(".*[a-z].*")) {
            return "Password must contain a lowercase letter.";
        }
        if (!value.matches(".*[0-9].*")) {
            return "Password must contain a number.";
        }
        if (!value.matches(".*[^A-Za-z0-9].*")) {
            return "Password must contain a special character.";
        }
        if (value.contains(" ")) {
            return "Password must not contain spaces.";
        }
        return null;
    }
}