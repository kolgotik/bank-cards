package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.service.AdminCardService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for administrative actions on bank cards.
 * Provides endpoints for listing, creating, blocking, unblocking, and deleting cards.
 * All methods are accessible only by users with the ADMIN role {@link Role}.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminCardController {

    private final AdminCardService adminCardService;

    /**
     * Retrieves a paginated list of all cards.
     *
     * @param pageable Pagination parameters (size, page, sort)
     * @return A page of {@link CardDTO} objects
     */
    @GetMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDTO>> getAllCards(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(adminCardService.getAllCards(pageable));
    }

    /**
     * Creates a new bank card based on the provided data.
     *
     * @param request The data used to create the card
     * @return The created card as a DTO {@link CardDTO}
     */
    @PostMapping("/card")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardCreationRequest request) {
        return ResponseEntity.ok(adminCardService.createCard(request));
    }

    /**
     * Retrieves information about a specific card by its ID.
     *
     * @param id The ID of the card
     * @return The card as a DTO {@link CardDTO}
     */
    @GetMapping("/card/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.getCardById(id));
    }

    /**
     * Blocks a card by its ID.
     *
     * @param id The ID of the card
     * @return The updated card as a DTO {@link CardDTO}
     */
    @PatchMapping("/card/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.blockCard(id));
    }


    /**
     * Unblocks a card by its ID.
     *
     * @param id The ID of the card
     * @return The updated card as a DTO {@link CardDTO}
     */
    @PatchMapping("/card/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> unblockCard(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.unblockCard(id));
    }

    /**
     * Deletes a card by its ID.
     *
     * @param id The ID of the card
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/card/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        adminCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
