package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RegistrationRequest {

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;

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

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}

