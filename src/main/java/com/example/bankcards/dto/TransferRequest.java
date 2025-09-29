package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferRequest {

    private final Long sourceCardId;
    private final Long targetCardId;
    private final BigDecimal amount;

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
