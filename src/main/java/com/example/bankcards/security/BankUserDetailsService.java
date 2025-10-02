package com.example.bankcards.security;

import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.repository.BankUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implementation of Spring Security's {@link UserDetailsService} that retrieves user details
 * from the database using the provided username.
 */
@Component
@AllArgsConstructor
public class BankUserDetailsService implements UserDetailsService {

    /**
     * Repository used to fetch user data from the database.
     */
    private final BankUserRepo bankUserRepo;

    /**
     * Loads a user by their username for Spring Security authentication.
     *
     * @param username the username of the user to load
     * @return a {@link UserDetails} object representing the user
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser bankUser = bankUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(bankUser.getUsername(), bankUser.getPassword(), Set.of(bankUser.getRole()));
    }
}
