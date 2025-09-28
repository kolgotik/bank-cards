package com.example.bankcards.exception;

import com.example.bankcards.exception.exceptions.InvalidUserDataException;
import com.example.bankcards.exception.exceptions.LoginException;
import com.example.bankcards.exception.exceptions.RegistrationException;
import com.example.bankcards.exception.exceptions.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class SecurityExceptionHandler {

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
        }
        else {
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException() {
        return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return new ResponseEntity<>("Bad credentials", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<String> handleRegistrationException(RegistrationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
