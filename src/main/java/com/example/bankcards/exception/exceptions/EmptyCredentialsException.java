package com.example.bankcards.exception.exceptions;

public class EmptyCredentialsException extends InvalidUserDataException {
    public EmptyCredentialsException(String message) {
        super(message);
    }
}
