package com.qwerty.pastebook.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.qwerty.pastebook.dto.auth.TokenDTO;
import com.qwerty.pastebook.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTokenProvider {

    private final Clock clock;
    private final Algorithm algorithm;

    @Value("${jwt.access_token.expiration}")
    private Integer accessTokenExpiration;

    public TokenDTO createTokenForUser(UserEntity user) {
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(clock.millis() + accessTokenExpiration))
                .sign(algorithm);

        return new TokenDTO(access_token);
    }
}
