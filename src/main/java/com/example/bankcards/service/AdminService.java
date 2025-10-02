package com.example.bankcards.service;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages administrative actions like promoting users to admin and creating new users.
 */
@Service
@AllArgsConstructor
public class AdminService {

    private final AuthService authService;
    private final BankUserService bankUserService;

    /**
     * Promotes a user to the ADMIN role.
     *
     * @param id the ID of the user to promote
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Transactional
    public void makeAdmin(Long id) {
        BankUser bankUser = bankUserService.getById(id).orElseThrow(
                () -> new UsernameNotFoundException("User does not exist"));
        if (bankUser.getRole() != Role.ADMIN) {
            bankUser.setRole(Role.ADMIN);
            bankUserService.saveUser(bankUser);
        }
    }

    /**
     * Creates a new user using the provided registration request.
     *
     * @param request {@link RegistrationRequest} request containing user details
     */
    @Transactional
    public void createUser(RegistrationRequest request) {
        authService.register(request);
    }
}
