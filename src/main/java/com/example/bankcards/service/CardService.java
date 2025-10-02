package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.exception.exceptions.CardDoesNotExistException;
import com.example.bankcards.exception.exceptions.CardException;
import com.example.bankcards.exception.exceptions.CardStatusException;
import com.example.bankcards.exception.exceptions.InsufficientFundsException;
import com.example.bankcards.repository.CardRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Handles card operations such as transfers, balance checks, blocking cards,
 * and scheduled updates for expired cards.
 */
@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    private final CardRepo cardRepo;
    private final BankUserService bankUserService;

    /**
     * Transfers funds from one card to another.
     *
     * @param authentication the current user's authentication context
     * @param sourceCardId   the ID of the source card
     * @param targetCardId   the ID of the target card
     * @param amount         the amount to transfer
     * @throws CardException            if the source and target cards are the same
     * @throws CardDoesNotExistException if either card does not exist
     * @throws CardStatusException      if either card is blocked or expired
     * @throws InsufficientFundsException if the source card has insufficient funds
     */
    @Transactional
    public void transferBetweenCards(Authentication authentication,
                                     Long sourceCardId,
                                     Long targetCardId,
                                     BigDecimal amount) {

        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);

        if (sourceCardId.equals(targetCardId)) {
            throw new CardException("Cannot transfer between the same card");
        }
        Card sourceCard = cardRepo.findById(sourceCardId).orElseThrow(
                () -> new CardDoesNotExistException("Source card does not exist"));
        Card targetCard = cardRepo.findById(targetCardId).orElseThrow(
                () -> new CardDoesNotExistException("Target card does not exist"));

        validateCardOwnership(sourceCard, bankUser.getId());
        validateCardOwnership(targetCard, bankUser.getId());

        validateCardStatus(sourceCard);
        validateCardStatus(targetCard);

        if (sourceCard.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds on source card");
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        targetCard.setBalance(targetCard.getBalance().add(amount));

        cardRepo.save(sourceCard);
        cardRepo.save(targetCard);
    }

    /**
     * Requests to block a card based on the current user's authentication.
     *
     * @param authentication the current user's authentication context
     * @param id             the ID of the card to block
     * @throws CardDoesNotExistException if the card does not exist
     * @throws CardStatusException       if the card is already blocked or expired
     */
    @Transactional
    public void requestBlockCard(Authentication authentication, Long id) {
        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);

        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));

        validateCardOwnership(card, bankUser.getId());

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardStatusException("Card is already blocked");
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new CardStatusException("Card is expired");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    /**
     * Retrieves all cards belonging to the authenticated user.
     *
     * @param authentication the current user's authentication context
     * @param pageable       pagination parameters
     * @return a page of cards as DTOs
     */
    public Page<CardDTO> getAllUserCards(Authentication authentication, Pageable pageable) {
        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);
        Page<Card> allByBankUser = cardRepo.findAllByBankUser(bankUser, pageable);
        return allByBankUser.map(CardDTO::fromEntity);
    }

    /**
     * Retrieves the balance of a specific card owned by the authenticated user.
     *
     * @param authentication the current user's authentication context
     * @param id             the ID of the card
     * @return the card as a DTO
     * @throws CardDoesNotExistException if the card does not exist
     * @throws CardException             if the user is not the owner of the card
     */
    public CardDTO getCardBalanceById(Authentication authentication, Long id) {
        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        validateCardOwnership(card, bankUser.getId());
        return CardDTO.fromEntity(card);
    }

    /**
     * Validates that the card is active and not expired or blocked.
     *
     * @param card the card to validate
     * @throws CardStatusException if the card is blocked or expired
     */
    private void validateCardStatus(Card card) {
        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.EXPIRED) {
            throw new CardStatusException("Card has blocked or expired status");
        }
    }

    /**
     * Validates that the card belongs to the specified user.
     *
     * @param card   the card to validate
     * @param userId the ID of the user who should own the card
     * @throws CardException if the user is not the owner of the card
     */
    private void validateCardOwnership(Card card, Long userId) {
        if (!Objects.equals(card.getBankUser().getId(), userId)) {
            throw new CardException("Invalid card owner");
        }
    }

    /**
     * Scheduled task to update the status of expired cards daily.
     * Cards whose expiration date is in the past are marked as expired.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    void updateExpiredCards() {
        LocalDate today = LocalDate.now();
        List<Card> activeCards = cardRepo.findByStatusAndExpirationDateBefore(
                CardStatus.ACTIVE,
                today
        );

        for (Card card : activeCards) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepo.save(card);
        }
    }
}