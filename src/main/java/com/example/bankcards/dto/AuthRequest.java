package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthRequest {

    private final String username;
    private final String password;

    @JsonCreator
    public AuthRequest(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = trimOrNull(username);
        this.password = trimOrNull(password);
    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
