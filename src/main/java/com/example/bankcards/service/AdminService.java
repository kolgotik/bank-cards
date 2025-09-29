package com.example.bankcards.service;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AdminService {

    private final AuthService authService;
    private final BankUserService bankUserService;

    @Transactional
    public void makeAdmin(Long id) {
        BankUser bankUser = bankUserService.getById(id).orElseThrow(
                () -> new UsernameNotFoundException("User does not exists"));
        if (bankUser.getRole() != Role.ADMIN) {
            bankUser.setRole(Role.ADMIN);
            bankUserService.saveUser(bankUser);
        }
    }

    @Transactional
    public void createUser(RegistrationRequest request) {
        authService.register(request);
    }
}
