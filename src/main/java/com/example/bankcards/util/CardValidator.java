package com.example.bankcards.util;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.entity.card.CardBINorIIN;

public class CardValidator {

    public static boolean isValidRequest(CardCreationRequest request) {
        if (request == null) {
            return false;
        } else if (request.getUserId() == null) {
            return false;
        } else if (request.getCardNumber() == null
                || request.getCardNumber().isBlank()
                || request.getCardNumber().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 19) {
            return false;
        }

        String regex = "^" + CardBINorIIN.getCardBINorIIN() + "-\\d{4}-\\d{4}-\\d{4}$";
        return cardNumber.matches(regex);
    }
}
