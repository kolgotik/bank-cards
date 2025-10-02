package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO used to represent a fund transfer request from one card to another.
 */
@Getter
public class TransferRequest {

    /**
     * The ID of the source card from which funds will be deducted.
     */
    private final Long sourceCardId;
    /**
     * The ID of the target card to which funds will be transferred.
     */
    private final Long targetCardId;
    /**
     * The amount to be transferred from the source card to the target card.
     */
    private final BigDecimal amount;

    /**
     * Constructs a new {@code TransferRequest} with the specified source card ID,
     * target card ID, and amount.
     *
     * @param sourceCardId   the ID of the source card
     * @param targetCardId   the ID of the target card
     * @param amount         the amount to be transferred
     */
    @JsonCreator
    public TransferRequest(
            @JsonProperty("sourceCardId") Long sourceCardId,
            @JsonProperty("targetCardId") Long targetCardId,
            @JsonProperty("amount") BigDecimal amount) {
        this.sourceCardId = sourceCardId;
        this.targetCardId = targetCardId;
        this.amount = amount;
    }
}
