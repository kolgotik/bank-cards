package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
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

/**
 * Handles card management operations for administrators.
 * Includes methods for creating, listing, blocking, unblocking, and deleting cards.
 */
@Service
@AllArgsConstructor
public class AdminCardService {

    private final CardRepo cardRepo;
    private final BankUserService bankUserService;

    /**
     * Creates a new bank card based on the {@link CardCreationRequest} data.
     * Validates the input and ensures the user is not an admin before creation.
     *
     * @param request the card creation request
     * @return the created card as a {@link CardDTO}
     * @throws CardCreationException if the request or card number is invalid
     * @throws CardAlreadyExistsException if a card with the same number already exists
     * @throws CardCreationException if the user is an admin (admins cannot own cards)
     */
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

        if (bankUser.getRole().equals(Role.ADMIN)) {
            throw new CardCreationException("Admin cannot own a card");
        }

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

    /**
     * Retrieves a paginated list of all cards in the system.
     *
     * @param pageable pagination parameters (size, page, sort)
     * @return a page of {@link CardDTO} objects
     */
    public Page<CardDTO> getAllCards(Pageable pageable) {
        return cardRepo.findAll(pageable).map(CardDTO::fromEntity);
    }

    /**
     * Retrieves a card by its ID.
     *
     * @param id the ID of the card
     * @return the card as a {@link CardDTO}
     * @throws CardDoesNotExistException if the card does not exist
     */
    public CardDTO getCardById(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        return CardDTO.fromEntity(card);
    }

    /**
     * Blocks a card by updating its {@link CardStatus} to BLOCKED.
     *
     * @param id the ID of the card
     * @return the updated card as a {@link CardDTO}
     * @throws CardDoesNotExistException if the card does not exist
     * @throws CardStatusException if the card is already blocked
     */
    @Transactional
    public CardDTO blockCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardStatusException("Card is already blocked");
        }
        card.setStatus(CardStatus.BLOCKED);
        Card save = cardRepo.save(card);
        return CardDTO.fromEntity(save);
    }

    /**
     * Unblocks a card by updating its {@link CardStatus} to ACTIVE.
     *
     * @param id the ID of the card
     * @return the updated card as a {@link CardDTO}
     * @throws CardDoesNotExistException if the card does not exist
     * @throws CardStatusException if the card is already active
     */
    @Transactional
    public CardDTO unblockCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new CardStatusException("Card is already active");
        }
        card.setStatus(CardStatus.ACTIVE);
        Card save = cardRepo.save(card);
        return CardDTO.fromEntity(save);
    }

    /**
     * Deletes a card from the database.
     *
     * @param id the ID of the card to delete
     * @throws CardDoesNotExistException if the card does not exist
     */
    @Transactional
    public void deleteCard(Long id) {
        Card card = cardRepo.findById(id).orElseThrow(
                () -> new CardDoesNotExistException("Card does not exist"));
        cardRepo.delete(card);
    }
}

