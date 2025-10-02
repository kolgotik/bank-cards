package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO used to represent the request data for creating a new card.
 */
@Getter
public class CardCreationRequest {

    /**
     * The card number. Trimming is applied if not null.
     */
    private final String cardNumber;
    /**
     * The ID of the user who will own the card.
     */
    private final Long userId;
    /**
     * The initial balance of the card. If null, it defaults to 0.
     */
    private final BigDecimal balance;

    /**
     * Constructs a new {@code CardCreationRequest} with the provided values.
     *
     * @param cardNumber the card number (will be trimmed if not null)
     * @param userId the user ID associated with the card
     * @param balance the initial balance (defaults to 0 if null)
     */
    @JsonCreator
    public CardCreationRequest(
            @JsonProperty("cardNumber") String cardNumber,
            @JsonProperty("userId") Long userId,
            @JsonProperty("balance") BigDecimal balance) {

        this.cardNumber = trimOrNull(cardNumber);
        this.userId = userId;
        this.balance = balance == null ? BigDecimal.ZERO : balance;

    }

    /**
     * Trims the given string if it is not null; otherwise returns null.
     *
     * @param value the string to trim
     * @return the trimmed string or null if input was null
     */
    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
