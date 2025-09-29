package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

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

    @PatchMapping("/card/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> requestBlockCard(@PathVariable Long id) {
        cardService.requestBlockCard(id);
        return ResponseEntity.ok("Request to block card sent successfully");
    }

    @GetMapping("/cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardDTO>> getAllUserCards(@PageableDefault Pageable pageable,
                                                         Authentication authentication) {
        return ResponseEntity.ok(cardService.getAllUserCards(authentication, pageable));
    }

    @GetMapping("/card/balance/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardDTO> getCardBalance(@PathVariable Long id,
                                                  Authentication authentication) {
        CardDTO cardBalanceById = cardService.getCardBalanceById(authentication, id);
        return ResponseEntity.ok(cardBalanceById);
    }
}
