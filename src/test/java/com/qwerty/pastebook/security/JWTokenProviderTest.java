package com.qwerty.pastebook.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.qwerty.pastebook.dto.auth.TokenDTO;
import com.qwerty.pastebook.entities.UserEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@RunWith(JUnit4.class)
class JWTokenProviderTest {

    @Value("${jwt.access_token.expiration}")
    private int accessTokenExpiration;

    private JWTokenProvider jwTokenProvider;

    @Mock
    private Clock clock;

    @Autowired
    private Algorithm algorithm;

    @BeforeEach
    @SneakyThrows
    public void init() {
        MockitoAnnotations.openMocks(this);

        jwTokenProvider = new JWTokenProvider(clock, algorithm);
        // TODO find a way to do better (without reflection)
        writeField(jwTokenProvider, "accessTokenExpiration", accessTokenExpiration, true);
    }

    @Test
    @DisplayName("Successful creating a token 1")
    void createTokenForUser() {
        //Before
        when(clock.millis()).thenReturn(150_000_000L);
        UserEntity userEntity = new UserEntity("username", "password");
        String rightToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTUwNjAwfQ.JyPXHgkllYd5obvu3nnFDbprmnOCo_PtU_aT9HVGV-U";
        //When
        TokenDTO token = jwTokenProvider.createTokenForUser(userEntity);
        //Then
        assertEquals(token.getToken(), rightToken);
    }

    @Test
    @DisplayName("Successful creating a token 2")
    void createTokenForUser2() {
        //Before
        when(clock.millis()).thenReturn(165_000_000L);
        UserEntity userEntity = new UserEntity("otherUsername", "password");
        String rightToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvdGhlclVzZXJuYW1lIiwiZXhwIjoxNjU2MDB9.D9wmn6fGMsPOBWokA2TGyEyt3oXa7Ql9AvI7scab_kc";
        //When
        TokenDTO token = jwTokenProvider.createTokenForUser(userEntity);
        //Then
        assertEquals(token.getToken(), rightToken);
    }
}