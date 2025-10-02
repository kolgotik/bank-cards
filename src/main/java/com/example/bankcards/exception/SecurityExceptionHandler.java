package com.example.bankcards.exception;

import com.example.bankcards.exception.exceptions.*;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Custom exception handler for security and domain-specific exceptions.
 * Provides meaningful HTTP responses for different types of errors.
 */
@Order(1)
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * Handles security-related exceptions such as JWT issues, login failures, or invalid user data.
     *
     * @param ex The exception that occurred
     * @return A response with the appropriate HTTP status and message
     */
    @ExceptionHandler({
            JwtException.class,
            LoginException.class,
            InvalidUserDataException.class
    })
    public ResponseEntity<String> handleSecurityExceptions(RuntimeException ex) {
        HttpStatus status;
        if (ex instanceof JwtException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof LoginException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof InvalidUserDataException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    /**
     * Handles exceptions related to card operations such as creation, existence, and status.
     *
     * @param ex The exception that occurred
     * @return A response with the appropriate HTTP status and message
     */
    @ExceptionHandler({
            CardException.class,
            CardStatusException.class,
            CardCreationException.class,
            CardDoesNotExistException.class,
            CardAlreadyExistsException.class,
            InsufficientFundsException.class
    })
    public ResponseEntity<String> handleCardException(CardException ex) {
        HttpStatus status;
        if (ex instanceof CardStatusException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof CardAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof CardCreationException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (ex instanceof CardDoesNotExistException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof InsufficientFundsException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    /**
     * Handles access denied exceptions thrown by Spring Security.
     *
     * @return A response with HTTP status 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException() {
        return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
    }

    /**
     * Handles bad credentials exceptions during authentication.
     *
     * @return A response with HTTP status 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return new ResponseEntity<>("Bad credentials", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles user already exists exceptions during registration.
     *
     * @param ex The exception that occurred
     * @return A response with HTTP status 409 (Conflict)
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles registration-related exceptions.
     *
     * @param ex The exception that occurred
     * @return A response with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleRegistrationException(RegistrationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
