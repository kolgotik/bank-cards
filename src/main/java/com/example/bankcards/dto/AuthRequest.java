package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Request object for authentication.
 * Contains the username and password provided by the user.
 */
@Getter
public class AuthRequest {

    /**
     * The username of the user attempting to log in.
     */
    private final String username;
    /**
     * The password of the user attempting to log in.
     */
    private final String password;

    /**
     * Constructs a new {@code AuthRequest} with the provided username and password.
     * Trimming is applied to both fields if they are not null.
     *
     * @param username the username (trimmed if not null)
     * @param password the password (trimmed if not null)
     */
    @JsonCreator
    public AuthRequest(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = trimOrNull(username);
        this.password = trimOrNull(password);
    }

    /**
     * Trims the given string if it is not null; otherwise returns null.
     *
     * @param value the string to trim
     * @return the trimmed string or null if input was null
     */
    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
