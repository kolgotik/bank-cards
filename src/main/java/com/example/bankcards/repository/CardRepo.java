package com.example.bankcards.repository;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.user.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepo extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String number);
    boolean existsById(Long id);
    Page<Card> findAllByBankUser(BankUser bankUser, Pageable pageable);

    List<Card> findByStatusAndExpirationDateBefore(CardStatus status, LocalDate expirationDate);
}
