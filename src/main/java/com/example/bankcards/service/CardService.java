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

@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    private final CardRepo cardRepo;
    private final BankUserService bankUserService;

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

    @Transactional
    public void requestBlockCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardStatusException("Card is already blocked");
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new CardStatusException("Card is expired");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepo.save(card);
    }

    public Page<CardDTO> getAllUserCards(Authentication authentication, Pageable pageable) {
        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);
        Page<Card> allByBankUser = cardRepo.findAllByBankUser(bankUser, pageable);
        return allByBankUser.map(CardDTO::fromEntity);
    }

    public CardDTO getCardBalanceById(Authentication authentication, Long id) {
        String name = authentication.getName();
        BankUser bankUser = bankUserService.getByUsername(name);
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        validateCardOwnership(card, bankUser.getId());
        return CardDTO.builder().balance(card.getBalance()).build();
    }

    private void validateCardStatus(Card card) {
        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.EXPIRED) {
            throw new CardStatusException("Card has blocked or expired status");
        }
    }

    private void validateCardOwnership(Card card, Long userId) {
        if (!Objects.equals(card.getBankUser().getId(), userId)) {
            throw new CardException("Invalid card owner");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void updateExpiredCards() {
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
