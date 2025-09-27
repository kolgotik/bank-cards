package com.example.bankcards.exception.exceptions;

public class UserDoesNotExistsException extends LoginException {
    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
