package com.example.bankcards.util;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.entity.card.CardBINorIIN;

/**
 * Utility class for validating card-related data such as card creation requests and card numbers.
 */
public class CardValidator {

    /**
     * Validates a card creation request by checking if all required fields are present.
     *
     * @param request the card creation request to validate
     * @return true if the request is valid, false otherwise
     */
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

    /**
     * Validates that a card number is in the correct format.
     * The format must be: BIN/IIN-XXXX-XXXX-XXXX, where BIN/IIN is predefined {@link CardBINorIIN},
     * and each segment is 4 digits long.
     *
     * @param cardNumber the card number to validate
     * @return true if the card number is valid, false otherwise
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 19) {
            return false;
        }

        String regex = "^" + CardBINorIIN.getCardBINorIIN() + "-\\d{4}-\\d{4}-\\d{4}$";
        return cardNumber.matches(regex);
    }
}
