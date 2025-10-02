package com.example.bankcards.util;

/**
 * Utility class for masking card numbers.
 */
public class CardUtil {

    /**
     * Masks a card number by hiding all digits except the last four.
     * Example input: "4000-5678-9012-3456"
     * Example output: "**** **** **** 3456"
     *
     * @param cardNumber the card number to mask
     * @return the masked card number
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }

        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFourDigits;
    }
}