package com.example.bankcards.entity.card;

import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.util.SimpleCardEncryptConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a bank card entity stored in the database.
 * Includes encryption for sensitive data like the card number.
 */
@Table(name = "cards")
@Entity
@Getter
@Setter
public class Card {

    public Card() {
    }

    /**
     * The unique identifier of the card in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The card number, which is encrypted using {@link SimpleCardEncryptConverter}.
     * Must be unique and non-null.
     */
    @Convert(converter = SimpleCardEncryptConverter.class)
    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    /**
     * The name of the card owner.
     * Must be non-null.
     */
    @Column(nullable = false)
    private String ownerName;

    /**
     * The expiration date of the card in the format YYYY-MM-DD.
     * Must be non-null.
     */
    @Column(nullable = false)
    private LocalDate expirationDate;

    /**
     * The current status of the card (e.g., ACTIVE, BLOCKED).
     * Must be non-null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    /**
     * The current balance on the card.
     * Must be non-null.
     */
    @Column(nullable = false)
    private BigDecimal balance;

    /**
     * The user who owns this card.
     * Relationship is maintained with lazy fetching and cascading operations.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade =
            {
                    CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.REFRESH, CascadeType.DETACH
            })
    @JoinColumn(name = "user_id")
    private BankUser bankUser;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Card card = (Card) object;
        return id.equals(card.id)
                && cardNumber.equals(card.cardNumber)
                && ownerName.equals(card.ownerName)
                && expirationDate.equals(card.expirationDate)
                && status == card.status
                && balance.equals(card.balance)
                && bankUser.equals(card.bankUser);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + cardNumber.hashCode();
        result = 31 * result + ownerName.hashCode();
        result = 31 * result + expirationDate.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + balance.hashCode();
        result = 31 * result + bankUser.hashCode();
        return result;
    }
}
