package com.example.bankcards.service;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.repository.BankUserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BankUserService {

    private final BankUserRepo bankUserRepo;

    private BankUser saveUser(BankUser bankUser) {
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

    public BankUser getByUsername(String username) {
        if (bankUserRepo.existsByUsername(username)) {
            return bankUserRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            throw new UserAlreadyExistsException("User does not exist");
        }
    }

    public BankUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

}
