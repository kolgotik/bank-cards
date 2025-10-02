package com.example.bankcards.dto;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.util.CardUtil;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for representing a bank card with masked card number and other relevant details.
 */
@Builder
@Getter
public class CardDTO {

    /**
     * The unique identifier of the card.
     */
    private final Long id;
    /**
     * The masked card number (e.g., "**** **** **** 1234").
     */
    private final String cardNumber;
    /**
     * The name of the card owner.
     */
    private final String ownerName;
    /**
     * The expiration date of the card.
     */
    private final LocalDate expirationDate;
    /**
     * The current status of the card (ACTIVE, BLOCKED, EXPIRED).
     */
    private final CardStatus status;
    /**
     * The current balance of the card.
     */
    private final BigDecimal balance;

    /**
     * Constructs a new {@code CardDTO} with the provided card details.
     *
     * @param id             the card's unique identifier
     * @param cardNumber     the card number (masked)
     * @param ownerName      the name of the card owner
     * @param expirationDate the card's expiration date
     * @param status         the current status of the card
     * @param balance        the card's current balance
     */
    public CardDTO(Long id, String cardNumber,
                   String ownerName,
                   LocalDate expirationDate,
                   CardStatus status,
                   BigDecimal balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.ownerName = ownerName;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }

    /**
     * Converts a {@link Card} entity into a {@code CardDTO}.
     *
     * @param card the entity to convert
     * @return a new instance of {@code CardDTO}, or null if the input is null
     */
    public static CardDTO fromEntity(Card card) {
        if (card != null) {
            return CardDTO.builder()
                    .id(card.getId())
                    .cardNumber(CardUtil.maskCardNumber(card.getCardNumber()))
                    .ownerName(card.getOwnerName())
                    .expirationDate(card.getExpirationDate())
                    .status(card.getStatus())
                    .balance(card.getBalance())
                    .build();
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CardDTO cardDTO = (CardDTO) object;
        return cardNumber.equals(cardDTO.cardNumber) && ownerName.equals(cardDTO.ownerName) && expirationDate.equals(cardDTO.expirationDate) && status == cardDTO.status && balance.equals(cardDTO.balance);
    }

    @Override
    public int hashCode() {
        int result = cardNumber.hashCode();
        result = 31 * result + ownerName.hashCode();
        result = 31 * result + expirationDate.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + balance.hashCode();
        return result;
    }
}