package com.example.bankcards.exception.exceptions;

public class InsufficientFundsException extends CardException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
