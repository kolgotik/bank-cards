package com.example.bankcards.repository;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Provides data access operations for the {@link Card} entity.
 */
@Repository
public interface CardRepo extends JpaRepository<Card, Long> {

    /**
     * Checks whether a card with the given card number already exists.
     *
     * @param number the card number to check
     * @return true if a card with this number exists, false otherwise
     */
    boolean existsByCardNumber(String number);

    /**
     * Checks whether a card with the given ID exists.
     *
     * @param id the card ID to check
     * @return true if a card with this ID exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Retrieves a paginated list of all cards owned by a specific user.
     *
     * @param bankUser the user whose cards are being fetched
     * @param pageable pagination parameters (size, page, sort)
     * @return a page of cards associated with the user
     */
    Page<Card> findAllByBankUser(BankUser bankUser, Pageable pageable);

    /**
     * Finds all cards with the specified status that have expired before the given date.
     *
     * @param status the card status to filter by
     * @param expirationDate the cutoff date for checking expiration
     * @return a list of cards matching the criteria
     */
    List<Card> findByStatusAndExpirationDateBefore(CardStatus status, LocalDate expirationDate);
}