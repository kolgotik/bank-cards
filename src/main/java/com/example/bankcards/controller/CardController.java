package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user-level operations on bank cards.
 * Provides endpoints to transfer money between cards, request card blocking,
 * and retrieve card balance and list of user's cards.
 * All methods are accessible only by users with the USER role {@link Role}.
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * Transfers funds between two cards.
     *
     * @param request The {@link TransferRequest} containing source card ID, target card ID, and amount
     * @param authentication Authentication object to identify the current user. Provided by Spring Security
     * @return ResponseEntity with a success message
     */
    @PostMapping("/card/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> transferBetweenCards(@RequestBody TransferRequest request,
                                                       Authentication authentication) {
        cardService.transferBetweenCards(
                authentication,
                request.getSourceCardId(),
                request.getTargetCardId(),
                request.getAmount());

        return ResponseEntity.ok("Transfer completed successfully");
    }

    /**
     * Block user's specific card.
     *
     * @param id The ID of the card to be blocked
     * @param authentication Authentication object to identify the current user. Provided by Spring Security
     * @return ResponseEntity with a success message
     */
    @PatchMapping("/card/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> requestBlockCard(@PathVariable Long id,
                                                   Authentication authentication) {
        cardService.requestBlockCard(authentication, id);
        return ResponseEntity.ok("Request to block card sent successfully");
    }

    /**
     * Retrieves a paginated list of all cards owned by the authenticated user.
     *
     * @param pageable Pagination parameters (size, page, sort)
     * @param authentication Authentication object to identify the current user. Provided by Spring Security
     * @return A page of {@link CardDTO} objects representing the user's cards
     */
    @GetMapping("/cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardDTO>> getAllUserCards(@PageableDefault Pageable pageable,
                                                         Authentication authentication) {
        return ResponseEntity.ok(cardService.getAllUserCards(authentication, pageable));
    }

    /**
     * Retrieves the balance of a specific user's card.
     *
     * @param id The ID of the card whose balance is requested
     * @param authentication Authentication object to identify the current user. Provided by Spring Security
     * @return The {@link CardDTO} object which includes the current balance
     */
    @GetMapping("/card/balance/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardDTO> getCardBalance(@PathVariable Long id,
                                                  Authentication authentication) {
        CardDTO cardBalanceById = cardService.getCardBalanceById(authentication, id);
        return ResponseEntity.ok(cardBalanceById);
    }
}
