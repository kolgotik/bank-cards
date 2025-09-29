package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.exception.exceptions.CardAlreadyExistsException;
import com.example.bankcards.exception.exceptions.CardCreationException;
import com.example.bankcards.exception.exceptions.CardDoesNotExistException;
import com.example.bankcards.exception.exceptions.CardStatusException;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.util.CardValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class AdminCardService {

    private final CardRepo cardRepo;
    private final BankUserService bankUserService;

    @Transactional
    public CardDTO createCard(CardCreationRequest request) {
        boolean validRequest = CardValidator.isValidRequest(request);
        if (!validRequest) {
            throw new CardCreationException("Invalid card creation request");
        }
        boolean validCardNumber = CardValidator.isValidCardNumber(request.getCardNumber());
        if (!validCardNumber) {
            throw new CardCreationException("Invalid card number");
        }
        boolean exists = cardRepo.existsByCardNumber(request.getCardNumber());
        if (exists) {
            throw new CardAlreadyExistsException("Card already exists");
        }

        BankUser bankUser = bankUserService.getById(request.getUserId())
                .orElseThrow(() -> new CardCreationException("User not found"));

        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setOwnerName(bankUser.getFirstName() + " " + bankUser.getLastName());
        card.setExpirationDate(LocalDate.now().plusYears(5));
        card.setBankUser(bankUser);

        cardRepo.save(card);

        return CardDTO.fromEntity(card);
    }

    public Page<CardDTO> getAllCards(Pageable pageable) {
        return cardRepo.findAll(pageable).map(CardDTO::fromEntity);
    }

    public CardDTO getCardById(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        return CardDTO.fromEntity(card);
    }

    @Transactional
    public CardDTO blockCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardStatusException("Card is already blocked");
        }
        card.setStatus(CardStatus.BLOCKED);
        Card save = cardRepo.save(card);
        return CardDTO.builder().status(save.getStatus()).build();
    }

    @Transactional
    public CardDTO unblockCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new CardStatusException("Card is already active");
        }
        card.setStatus(CardStatus.ACTIVE);
        Card save = cardRepo.save(card);
        return CardDTO.builder().status(save.getStatus()).build();
    }

    @Transactional
    public void deleteCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        cardRepo.delete(card);
    }

}
