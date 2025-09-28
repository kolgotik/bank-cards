package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.exception.exceptions.EmptyCredentialsException;
import com.example.bankcards.exception.exceptions.RegistrationException;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import com.example.bankcards.exception.exceptions.UserDoesNotExistsException;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.BankUserService;
import com.example.bankcards.util.BankUserValidator;
import com.example.bankcards.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private BankUserService bankUserService;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister_ShouldReturnJwtToken() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testuser", "password123", "John", "Doe");
        AuthResponse response = new AuthResponse("mocked-jwt-token");

        when(authService.register(argThat(r ->
                "testuser".equals(r.getUsername()) &&
                        "password123".equals(r.getPassword()) &&
                        "John".equals(r.getFirstName()) &&
                        "Doe".equals(r.getLastName())
        ))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("mocked-jwt-token"));

        verify(authService).register(argThat(r ->
                "testuser".equals(r.getUsername()) &&
                        "password123".equals(r.getPassword()) &&
                        "John".equals(r.getFirstName()) &&
                        "Doe".equals(r.getLastName())
        ));
    }

    @Test
    void testRegister_WithInvalidCreds_ShouldThrowEmptyCredentialsException() throws Exception {
        RegistrationRequest request = new RegistrationRequest("", "", "test", "test");

        when(authService.register(argThat(r ->
                !BankUserValidator.isValidUsernameAndPassword(r.getUsername(), r.getPassword())
        ))).thenThrow(new EmptyCredentialsException("Username or password is empty"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("Username or password is empty")));

        verify(authService).register(argThat(r ->
                !BankUserValidator.isValidUsernameAndPassword(r.getUsername(), r.getPassword())));
    }

    @Test
    void testRegister_WithInvalidFirstNameLastName_ShouldThrowRegistrationException() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Test", "pass", "", "");

        when(authService.register(argThat(r ->
                !BankUserValidator.isValidFirstName(r.getFirstName()) ||
                        !BankUserValidator.isValidLastName(r.getLastName())
        ))).thenThrow(new RegistrationException("First name or last name can't be empty"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("First name or last name can't be empty")));

        verify(authService).register(argThat(r ->
                !BankUserValidator.isValidFirstName(r.getFirstName()) ||
                        !BankUserValidator.isValidLastName(r.getLastName())));
    }

    @Test
    void testRegister_WithExistingUsername_ShouldThrowUserAlreadyExistsException() throws Exception {
        RegistrationRequest request = new RegistrationRequest("testuser", "password123", "John", "Doe");

        when(authService.register(argThat(r ->
                "testuser".equals(r.getUsername())
        ))).thenThrow(new UserAlreadyExistsException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Username already exists")));

        verify(authService).register(argThat(r ->
                "testuser".equals(r.getUsername())
        ));
    }

    @Test
    void testLogin_ShouldReturnJwtToken() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password123");
        AuthResponse response = new AuthResponse("mocked-jwt-token");

        when(authService.login(argThat(r ->
                "testuser".equals(r.getUsername()) &&
                        "password123".equals(r.getPassword())
        ))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("mocked-jwt-token"));

        verify(authService).login(argThat(r ->
                "testuser".equals(r.getUsername()) &&
                        "password123".equals(r.getPassword())
        ));
    }

    @Test
    void testLogin_WithInvalidCreds_ShouldThrowEmptyCredentialsException() throws Exception {
        AuthRequest request = new AuthRequest("", "pass");

        AuthRequest authRequest = argThat(r ->
                !BankUserValidator.isValidUsernameAndPassword(r.getUsername(), r.getPassword()));
        when(authService.login(authRequest)).thenThrow(new EmptyCredentialsException("Username or password is empty"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("Username or password is empty")));

        verify(authService).login(argThat(r ->
                !BankUserValidator.isValidUsernameAndPassword(r.getUsername(), r.getPassword())));
    }

    @Test
    void testLogin_WithNonExistingUsername_ShouldThrowUserDoesNotExistsException() throws Exception {
        AuthRequest request = new AuthRequest("nonexisting", "pass");

        when(authService.login(argThat(r ->
                "nonexisting".equals(r.getUsername()) &&
                        "pass".equals(r.getPassword())
        ))).thenThrow(new UserDoesNotExistsException("User does not exist"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("User does not exist")));

        verify(authService).login(argThat(r ->
                "nonexisting".equals(r.getUsername()) &&
                        "pass".equals(r.getPassword())
        ));
    }
}
