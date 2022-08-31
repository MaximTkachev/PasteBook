package com.qwerty.pastebook.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class TokenDTO {
    private final String token;

    @JsonCreator
    public TokenDTO(@JsonProperty("token") String token) {
        this.token = token;
    }
}
