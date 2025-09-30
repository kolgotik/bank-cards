package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreationRequest;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.exception.exceptions.CardDoesNotExistException;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.util.CardUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceTest {

    @InjectMocks
    private AdminCardService adminCardService;

    @Mock
    private CardRepo cardRepo;

    @Mock
    private BankUserService bankUserService;

    private CardCreationRequest request;
    private Card card;
    private BankUser user;

    @BeforeEach
    void setUp() {
        request = new CardCreationRequest(
                "4000-0000-0000-0000",
                1L,
                BigDecimal.valueOf(1000));

        user = new BankUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");

        card = new Card();
        card.setId(1L);
        card.setCardNumber("4000-0000-0000-0000");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(CardStatus.ACTIVE);
        card.setOwnerName("John Doe");
        card.setExpirationDate(LocalDate.now().plusYears(5));
        card.setBankUser(user);
    }

    @Test
    void testCreateCard_Success() {
        // Arrange
        when(bankUserService.getById(request.getUserId())).thenReturn(Optional.of(user));
        when(cardRepo.existsByCardNumber(request.getCardNumber())).thenReturn(false);
        when(cardRepo.save(any(Card.class))).thenReturn(card);
        user.setRole(Role.USER);

        // Act
        CardDTO result = adminCardService.createCard(request);

        // Assert
        assertNotNull(result);
        assertEquals(CardUtil.maskCardNumber(card.getCardNumber()), result.getCardNumber());
        assertEquals(card.getBalance(), result.getBalance());
        assertEquals(card.getOwnerName(), result.getOwnerName());
        assertEquals(card.getStatus(), result.getStatus());
        assertEquals(card.getExpirationDate(), result.getExpirationDate());
    }

    @Test
    void testGetAllCards() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepo.findAll(pageable)).thenReturn(page);

        // Act
        Page<CardDTO> result = adminCardService.getAllCards(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(CardUtil.maskCardNumber(card.getCardNumber()), result.getContent().get(0).getCardNumber());
    }

    @Test
    void testGetCardById_CardDoesNotExist() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardDoesNotExistException.class, () -> adminCardService.getCardById(1L));
    }

    @Test
    void testGetCardById_Success() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));

        // Act
        CardDTO result = adminCardService.getCardById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(CardUtil.maskCardNumber(card.getCardNumber()), result.getCardNumber());
        assertEquals(card.getBalance(), result.getBalance());
        assertEquals(card.getOwnerName(), result.getOwnerName());
        assertEquals(card.getStatus(), result.getStatus());
        assertEquals(card.getExpirationDate(), result.getExpirationDate());
    }

    @Test
    void testBlockCard_Success() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepo.save(card)).thenReturn(card);

        // Act
        CardDTO result = adminCardService.blockCard(1L);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
    }

    @Test
    void testUnblockCard_Success() {
        // Arrange
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepo.save(card)).thenReturn(card);

        // Act
        CardDTO result = adminCardService.unblockCard(1L);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testDeleteCard_Success() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));

        // Act
        adminCardService.deleteCard(1L);

        // Assert
        verify(cardRepo, times(1)).delete(card);
    }
}
