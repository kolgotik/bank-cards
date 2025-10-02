package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.entity.user.Role;
import com.example.bankcards.exception.exceptions.EmptyCredentialsException;
import com.example.bankcards.exception.exceptions.InvalidUserDataException;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.exception.exceptions.UserDoesNotExistException;
import com.example.bankcards.util.BankUserValidator;
import com.example.bankcards.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user authentication and registration logic.
 * Integrates with Spring Security for password encryption and token generation.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final BankUserService bankUserService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user based on the provided {@link RegistrationRequest} data.
     * Validates the input and generates a JWT token upon successful registration.
     *
     * @param authRequest the registration request containing username, password, first name, and last name
     * @return an {@link AuthResponse} containing the generated JWT token
     * @throws UserAlreadyExistsException if a user with the same username already exists
     * @throws EmptyCredentialsException  if username or password is empty
     * @throws InvalidUserDataException   if first or last name is missing
     */
    @Transactional
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

    /**
     * Authenticates a user using their username and password from {@link AuthRequest}.
     * If authentication is successful, a JWT token is generated.
     *
     * @param authRequest the login request containing username and password
     * @return an {@link AuthResponse} containing the generated JWT token
     * @throws EmptyCredentialsException  if username or password is empty
     * @throws UserDoesNotExistException  if the user does not exist
     */
    @Transactional
    public AuthResponse login(AuthRequest authRequest) {
        validateLoginRequest(authRequest);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                ));

        log.info("User is authenticated");
        String jwt = jwtUtil.generateToken(authentication.getName());
        return new AuthResponse(jwt);
    }

    /**
     * Validates the {@link RegistrationRequest} by checking for existing users
     * and ensuring that all required fields are present and valid.
     *
     * @param request the registration request to validate
     * @throws UserAlreadyExistsException if a user with the same username already exists
     * @throws EmptyCredentialsException  if username or password is empty
     * @throws InvalidUserDataException   if first or last name is missing
     */
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

    /**
     * Validates the {@link AuthRequest} by checking if the user exists and
     * if both username and password are non-empty.
     *
     * @param request the login request to validate
     * @throws EmptyCredentialsException  if username or password is empty
     * @throws UserDoesNotExistException  if the user does not exist
     */
    private void validateLoginRequest(AuthRequest request) {
        boolean valid = BankUserValidator.isValidUsernameAndPassword(request.getUsername(), request.getPassword());
        if (!valid) {
            throw new EmptyCredentialsException("Username or password is empty");
        }
        boolean exists = bankUserService.existsByUsername(request.getUsername());
        if (!exists) {
            throw new UserDoesNotExistException("User does not exist");
        }
    }
}
