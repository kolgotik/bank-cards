package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.user.BankUser;
import com.example.bankcards.exception.exceptions.*;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthServiceTest {

    @Mock
    private BankUserService bankUserService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void testRegister_WithValidData_ShouldReturnAuthResponse() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest("testuser", "password123", "John", "Doe");
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(bankUserService.existsByUsername("testuser")).thenReturn(false);
        when(jwtUtil.generateToken("testuser")).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getJwt());
        verify(bankUserService, times(1)).createUser(any(BankUser.class));
    }

    @Test
    void testRegister_UserAlreadyExists_ShouldThrowException() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest("testuser", "password123", "John", "Doe");
        when(bankUserService.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void testRegister_EmptyCredentials_ShouldThrowException() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest("", "", "", "");

        // Act & Assert
        assertThrows(EmptyCredentialsException.class, () -> authService.register(request));
    }

    @Test
    void testRegister_MissingNameFields_ShouldThrowException() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest("testuser", "password123", "", "");

        // Act & Assert
        assertThrows(InvalidUserDataException.class, () -> authService.register(request));
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        AuthRequest request = new AuthRequest("testuser", "password123");
        when(bankUserService.existsByUsername("testuser")).thenReturn(true);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken("testuser")).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getJwt());
    }

    @Test
    void testLogin_InvalidCredentials_ShouldThrowEmptyCredentialsException() {
        // Arrange
        AuthRequest request = new AuthRequest("", "wrongpassword");

        // Act & Assert
        assertThrows(EmptyCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_UserDoesNotExist_ShouldThrowUserDoesNotExistsException() {
        // Arrange
        AuthRequest request = new AuthRequest("testuser", "password123");
        when(bankUserService.existsByUsername("testuser")).thenReturn(false);

        // Act & Assert
        assertThrows(UserDoesNotExistsException.class, () -> authService.login(request));
    }
}
