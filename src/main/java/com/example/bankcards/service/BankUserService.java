package com.example.bankcards.service;

import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.repository.BankUserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BankUserService {

    private final BankUserRepo bankUserRepo;

    public BankUser saveUser(BankUser bankUser) {
        return bankUserRepo.save(bankUser);
    }

    public boolean existsByUsername(String username) {
        return bankUserRepo.existsByUsername(username);
    }

    public BankUser createUser(BankUser bankUser) {

        if (bankUserRepo.existsByUsername(bankUser.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        return saveUser(bankUser);
    }

    public Optional<BankUser> getById(Long id) {
        return bankUserRepo.findById(id);
    }

    public BankUser getByUsername(String username) {
        return bankUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public BankUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

}
