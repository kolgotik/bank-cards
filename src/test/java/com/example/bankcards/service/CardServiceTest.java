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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepo cardRepo;

    @Mock
    private BankUserService bankUserService;

    @Mock
    private Authentication authentication;

    @Mock
    private ApplicationEventPublisher publisher;

    private Card sourceCard;
    private Card targetCard;

    @Mock
    private BankUser bankUser;

    @BeforeEach
    void setUp() {
        bankUser = new BankUser();
        bankUser.setId(1L);
        bankUser.setUsername("testuser");

        sourceCard = new Card();
        sourceCard.setId(1L);
        sourceCard.setBalance(BigDecimal.valueOf(1000));
        sourceCard.setStatus(CardStatus.ACTIVE);
        sourceCard.setBankUser(bankUser);

        targetCard = new Card();
        targetCard.setId(2L);
        targetCard.setBalance(BigDecimal.ZERO);
        targetCard.setStatus(CardStatus.ACTIVE);
        targetCard.setBankUser(bankUser);
    }

    @Test
    void testTransferBetweenCards_Success() {
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(targetCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), sourceCard.getBalance());
        assertEquals(BigDecimal.valueOf(500), targetCard.getBalance());

        verify(cardRepo, times(1)).save(sourceCard);
        verify(cardRepo, times(1)).save(targetCard);
    }

    @Test
    void testTransferBetweenCards_SameCard() {
        when(authentication.getName()).thenReturn("testuser");

        assertThrows(CardException.class, () -> cardService.transferBetweenCards(authentication, 1L, 1L, BigDecimal.ONE));
    }

    @Test
    void testTransferBetweenCards_SourceCardDoesNotExist() {
        when(cardRepo.findById(1L)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("testuser");

        assertThrows(CardDoesNotExistException.class, () -> cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.ONE));
    }

    @Test
    void testTransferBetweenCards_TargetCardDoesNotExist() {
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepo.findById(2L)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("testuser");

        assertThrows(CardDoesNotExistException.class, () -> cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.ONE));
    }

    @Test
    void testTransferBetweenCards_InvalidCardOwnership() {
        BankUser otherUser = new BankUser();
        otherUser.setId(2L);
        sourceCard.setBankUser(otherUser);

        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(targetCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act & Assert
        assertThrows(CardException.class, () -> cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.ONE));
    }

    @Test
    void testTransferBetweenCards_CardIsBlocked() {
        sourceCard.setStatus(CardStatus.BLOCKED);

        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(targetCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act & Assert
        assertThrows(CardStatusException.class, () -> cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.ONE));
    }

    @Test
    void testTransferBetweenCards_InsufficientFunds() {
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(targetCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> cardService.transferBetweenCards(authentication, 1L, 2L, BigDecimal.valueOf(2000)));
    }

    @Test
    void testRequestBlockCard_Success() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(authentication.getName()).thenReturn(bankUser.getUsername());
        when(bankUserService.getByUsername(bankUser.getUsername())).thenReturn(bankUser);

        // Act
        cardService.requestBlockCard(authentication, 1L);

        // Assert
        assertEquals(CardStatus.BLOCKED, sourceCard.getStatus());
        verify(cardRepo, times(1)).save(sourceCard);
    }

    @Test
    void testRequestBlockCard_CardAlreadyBlocked() {
        // Arrange
        sourceCard.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(authentication.getName()).thenReturn(bankUser.getUsername());
        when(bankUserService.getByUsername(bankUser.getUsername())).thenReturn(bankUser);

        // Act & Assert
        assertThrows(CardStatusException.class, () -> cardService.requestBlockCard(authentication, 1L));
    }

    @Test
    void testRequestBlockCard_CardExpired() {
        // Arrange
        sourceCard.setStatus(CardStatus.EXPIRED);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(authentication.getName()).thenReturn(bankUser.getUsername());
        when(bankUserService.getByUsername(bankUser.getUsername())).thenReturn(bankUser);

        // Act & Assert
        assertThrows(CardStatusException.class, () -> cardService.requestBlockCard(authentication, 1L));
    }

    @Test
    void testGetAllUserCards_Success() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Page<Card> page = new PageImpl<>(List.of(sourceCard, targetCard));
        when(cardRepo.findAllByBankUser(bankUser, pageable)).thenReturn(page);
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act
        Page<CardDTO> result = cardService.getAllUserCards(authentication, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(sourceCard.getCardNumber(), result.getContent().get(0).getCardNumber());
    }

    @Test
    void testGetCardBalanceById_Success() {
        // Arrange
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act
        CardDTO result = cardService.getCardBalanceById(authentication, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(sourceCard.getBalance(), result.getBalance());
    }

    @Test
    void testGetCardBalanceById_InvalidCardOwner() {
        // Arrange
        BankUser otherUser = new BankUser();
        otherUser.setId(2L);
        sourceCard.setBankUser(otherUser);
        when(cardRepo.findById(1L)).thenReturn(Optional.of(sourceCard));
        when(authentication.getName()).thenReturn("testuser");
        when(bankUserService.getByUsername("testuser")).thenReturn(bankUser);

        // Act & Assert
        assertThrows(CardException.class, () -> cardService.getCardBalanceById(authentication, 1L));
    }
}

