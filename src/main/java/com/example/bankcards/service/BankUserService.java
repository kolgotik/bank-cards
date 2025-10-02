package com.example.bankcards.service;

import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.repository.BankUserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles operations for the BankUser entity.
 * Ensures uniqueness of usernames and provides access to user data.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BankUserService {

    private final BankUserRepo bankUserRepo;

    /**
     * Saves a new or existing user to the database.
     *
     * @param bankUser the user to save
     * @return the saved user
     */
    public BankUser saveUser(BankUser bankUser) {
        return bankUserRepo.save(bankUser);
    }

    /**
     * Checks if a user with the given username already exists.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return bankUserRepo.existsByUsername(username);
    }

    /**
     * Creates a new user after verifying that no user with the same username exists.
     *
     * @param bankUser the user to create
     * @return the created user
     * @throws UserAlreadyExistsException if a user with the same username already exists
     */
    public BankUser createUser(BankUser bankUser) {
        if (bankUserRepo.existsByUsername(bankUser.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        return saveUser(bankUser);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return an optional containing the user if found, or empty otherwise
     */
    public Optional<BankUser> getById(Long id) {
        return bankUserRepo.findById(id);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user
     * @return the user if found
     * @throws UsernameNotFoundException if the user does not exist
     */
    public BankUser getByUsername(String username) {
        return bankUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
