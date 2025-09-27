package com.example.bankcards.repository;

import com.example.bankcards.entity.user.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankUserRepo extends JpaRepository<BankUser, Long> {
    Optional<BankUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
