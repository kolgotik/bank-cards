package com.example.bankcards.exception.exceptions;

public class UserDoesNotExistException extends LoginException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
