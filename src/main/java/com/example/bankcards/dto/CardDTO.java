package com.example.bankcards.dto;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.util.CardUtil;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class CardDTO {

    private final String cardNumber;
    private final String ownerName;
    private final LocalDate expirationDate;
    private final CardStatus status;
    private final BigDecimal balance;

    public CardDTO(String cardNumber,
                   String ownerName,
                   LocalDate expirationDate,
                   CardStatus status,
                   BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.ownerName = ownerName;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }

    public static CardDTO fromEntity(Card card) {
        if (card != null) {
            return CardDTO.builder()
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