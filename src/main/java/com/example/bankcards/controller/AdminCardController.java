package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.AdminCardService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDTO>> getAllCards(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(adminCardService.getAllCards(pageable));
    }

    @PostMapping("/card")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardCreationRequest request) {
        return ResponseEntity.ok(adminCardService.createCard(request));
    }

    @GetMapping("/card/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.getCardById(id));
    }

    @PatchMapping("/card/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.blockCard(id));
    }


    @PatchMapping("/card/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> unblockCard(@PathVariable Long id) {
        return ResponseEntity.ok(adminCardService.unblockCard(id));
    }

    @DeleteMapping("/card/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        adminCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
