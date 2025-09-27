package com.example.bankcards.exception.exceptions;

public class UserAlreadyExistsException extends RegistrationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
