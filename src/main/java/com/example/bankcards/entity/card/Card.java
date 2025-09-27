package com.example.bankcards.entity.card;

import com.example.bankcards.entity.user.BankUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "cards")
@Entity
@Getter
@Setter
public class Card {

    public Card() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

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
