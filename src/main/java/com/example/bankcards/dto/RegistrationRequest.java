package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * DTO that encapsulates user registration information such as username,
 * password, first name, and last name.
 */
@Getter
public class RegistrationRequest {

    /**
     * The username chosen by the user.
     */
    private final String username;

    /**
     * The password provided by the user.
     */
    private final String password;

    /**
     * The first name of the user.
     */
    private final String firstName;

    /**
     * The last name of the user.
     */
    private final String lastName;

    /**
     * Constructs a new {@code RegistrationRequest} with the provided user details.
     * Trims all string fields if they are not null.
     *
     * @param username   the username provided by the user
     * @param password   the password provided by the user
     * @param firstName  the first name of the user
     * @param lastName   the last name of the user
     */
    @JsonCreator
    public RegistrationRequest(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName) {
        this.username = trimOrNull(username);
        this.password = trimOrNull(password);
        this.firstName = trimOrNull(firstName);
        this.lastName = trimOrNull(lastName);
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

