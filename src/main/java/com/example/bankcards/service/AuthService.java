package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.exception.exceptions.EmptyCredentialsException;
import com.example.bankcards.exception.exceptions.InvalidUserDataException;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.exception.exceptions.UserDoesNotExistsException;
import com.example.bankcards.util.BankUserValidator;
import com.example.bankcards.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final BankUserService bankUserService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegistrationRequest authRequest) {
        validateRegisterRequest(authRequest);
        BankUser user = BankUser.builder()
                .username(authRequest.getUsername())
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .role(Role.USER)
                .build();

        bankUserService.createUser(user);
        String jwt = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(jwt);
    }

    public AuthResponse login(AuthRequest authRequest) {
        validateLoginRequest(authRequest);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                ));

        log.info("User is authenticated");
        String jwt = jwtUtil.generateToken(authentication.getName());
        log.info("Generated JWT token: {}", jwt);
        return new AuthResponse(jwt);
    }

    private void validateRegisterRequest(RegistrationRequest request) {
        boolean exists = bankUserService.existsByUsername(request.getUsername());
        if (exists) {
            throw new UserAlreadyExistsException("User already exists");
        } else {
            boolean valid = BankUserValidator.isValidUsernameAndPassword(request.getUsername(), request.getPassword());
            boolean validFirstName = BankUserValidator.isValidFirstName(request.getFirstName());
            boolean validLastName = BankUserValidator.isValidLastName(request.getLastName());
            if (!valid) {
                throw new EmptyCredentialsException("Username or password is empty");
            } else if (!validFirstName || !validLastName) {
                throw new InvalidUserDataException("First name and last name can't be empty");
            }
        }
    }

    private void validateLoginRequest(AuthRequest request) {
        boolean valid = BankUserValidator.isValidUsernameAndPassword(request.getUsername(), request.getPassword());
        if (!valid) {
            throw new EmptyCredentialsException("Username or password is empty");
        }
        boolean exists = bankUserService.existsByUsername(request.getUsername());
        if (!exists) {
            throw new UserDoesNotExistsException("User does not exist");
        }
    }
}

