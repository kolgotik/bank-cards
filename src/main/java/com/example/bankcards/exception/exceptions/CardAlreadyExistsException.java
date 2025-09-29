package com.example.bankcards.exception.exceptions;

public class CardAlreadyExistsException extends CardCreationException {
    public CardAlreadyExistsException(String message) {
        super(message);
    }
}
