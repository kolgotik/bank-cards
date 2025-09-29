package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CardCreationRequest {

    private final String cardNumber;
    private final Long userId;
    private final BigDecimal balance;

    @JsonCreator
    public CardCreationRequest(
            @JsonProperty("cardNumber") String cardNumber,
            @JsonProperty("userId") Long userId,
            @JsonProperty("balance") BigDecimal balance) {

        this.cardNumber = trimOrNull(cardNumber);
        this.userId = userId;
        this.balance = balance == null ? BigDecimal.ZERO : balance;

    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
