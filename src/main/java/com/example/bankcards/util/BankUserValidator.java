package com.example.bankcards.util;

public class BankUserValidator {

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
