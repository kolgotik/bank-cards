package com.example.bankcards.util;

public class CardUtil {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }

        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFourDigits;
    }
}