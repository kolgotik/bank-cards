package com.example.bankcards.exception.exceptions;

public class CardDoesNotExistException extends CardException {
    public CardDoesNotExistException(String message) {
        super(message);
    }
}
