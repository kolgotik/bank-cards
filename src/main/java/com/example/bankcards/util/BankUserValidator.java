package com.example.bankcards.util;

/**
 * Utility class for validating user-related data such as username, password, first name, and last name.
 */
public class BankUserValidator {

    /**
     * Validates that both the username and password are not null or blank.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return true if both are valid, false otherwise
     */
    public static boolean isValidUsernameAndPassword(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        if (username.isBlank() || password.isBlank() ||
                username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Validates that the username is not null, blank, or empty.
     *
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        if (username.isBlank() || username.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Validates that the first name is not null, blank, or empty.
     *
     * @param firstName the first name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidFirstName(String firstName) {
        if (firstName == null) {
            return false;
        }
        if (firstName.isBlank() || firstName.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Validates that the last name is not null, blank, or empty.
     *
     * @param lastName the last name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLastName(String lastName) {
        if (lastName == null) {
            return false;
        }
        if (lastName.isBlank() || lastName.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
