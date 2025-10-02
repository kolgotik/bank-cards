package com.example.bankcards.dto;

import lombok.Getter;


/**
 * Response object returned after a successful authentication.
 * Contains the JSON Web Token (JWT) issued to the user.
 */
@Getter
public class AuthResponse {

    /**
     * The JWT token issued to the user upon successful authentication.
     */
    private final String jwt;

    /**
     * Constructs a new {@code AuthResponse} with the provided JWT token.
     *
     * @param jwt the JWT token to be returned to the client
     */
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}
