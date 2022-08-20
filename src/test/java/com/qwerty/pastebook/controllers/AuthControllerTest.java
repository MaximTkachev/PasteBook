package com.qwerty.pastebook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwerty.pastebook.dto.auth.LoginDTO;
import com.qwerty.pastebook.dto.auth.RegisterDTO;
import com.qwerty.pastebook.entities.UserEntity;
import com.qwerty.pastebook.repositories.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("Unsuccessful registration with an empty username")
    @SneakyThrows
    void registerWithEmptyUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("", "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username cannot be blank"));
    }

    @Test
    @DisplayName("Unsuccessful registration with a blank username")
    @SneakyThrows
    void registerWithBlankUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("    ", "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username cannot be blank"));
    }

    @Test
    @DisplayName("Unsuccessful registration with a null username")
    @SneakyThrows
    void registerWithNullUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO(null, "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username cannot be blank"));
    }

    @Test
    @DisplayName("Successful registration with a short username")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void registerWithShortUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("a", "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Successful registration with a long username")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void registerWithLongUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("a".repeat(255), "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Unsuccessful registration with too long username")
    @SneakyThrows
    void registerWithTooLongUsername() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("a".repeat(256), "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username length cannot be more than 255 symbols"));
    }

    @Test
    @DisplayName("Unsuccessful registration with too short password")
    @SneakyThrows
    void registerWithTooShortPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("username", "a".repeat(7)));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("password length cannot be less than 8 and more than 255 symbols"));
    }

    @Test
    @DisplayName("Successful registration with a short password")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void registerWithShortPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("username", "a".repeat(8)));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Successful registration with a long password")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void registerWithLongPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("username", "a".repeat(255)));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Unsuccessful registration with too long password")
    @SneakyThrows
    void registerWithTooLongPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("username", "a".repeat(256)));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("password length cannot be less than 8 and more than 255 symbols"));
    }

    @Test
    @DisplayName("Unsuccessful registration with null username and password")
    @SneakyThrows
    void registerWithNullUsernameAndPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO(null, null));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("several fields failed validation"))
                .andExpect(jsonPath("$.username").value("username cannot be blank"))
                .andExpect(jsonPath("$.password").value("password length cannot be less than 8 and more than 255 symbols"));
    }

    @Test
    @DisplayName("Unsuccessful registration with too long username and too short password")
    @SneakyThrows
    void registerWithTooLongUsernameAndTooShortPassword() {
        //Before
        String dto = objectMapper.writeValueAsString(new RegisterDTO("a".repeat(256), "a".repeat(7)));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("several fields failed validation"))
                .andExpect(jsonPath("$.username").value("username length cannot be more than 255 symbols"))
                .andExpect(jsonPath("$.password").value("password length cannot be less than 8 and more than 255 symbols"));
    }
    @Test
    @DisplayName("Unsuccessful registration with already occupied username")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void registerWithAlreadyOccupiedUsername(){
        //Before
        UserEntity user = new UserEntity("username", "password");
        userRepository.save(user);
        String dto = objectMapper.writeValueAsString(new RegisterDTO("username", "some_password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("username has already taken"));
    }

    @Test
    @DisplayName("Successful login")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void login() {
        //Before
        UserEntity user = new UserEntity("username", bCryptPasswordEncoder.encode("password"));
        userRepository.save(user);
        String dto = objectMapper.writeValueAsString(new LoginDTO("username", "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/login").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Unsuccessful login with wrong username")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void loginWithWrongUsername() {
        //Before
        UserEntity user = new UserEntity("username", bCryptPasswordEncoder.encode("password"));
        userRepository.save(user);
        String dto = objectMapper.writeValueAsString(new LoginDTO("anotherUsername", "password"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/login").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("username and/or password invalid"));
    }

    @Test
    @DisplayName("Unsuccessful login with wrong password")
    @SneakyThrows
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void loginWithWrongPassword() {
        //Before
        UserEntity user = new UserEntity("username", bCryptPasswordEncoder.encode("password"));
        userRepository.save(user);
        String dto = objectMapper.writeValueAsString(new LoginDTO("username", "anotherPassword"));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/login").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("username and/or password invalid"));
    }

        @Test
        @DisplayName("Unsuccessful login with wrong username and password")
        @SneakyThrows
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void loginWithWrongUsernameAndPassword() {
            //Before
            UserEntity user = new UserEntity("username", bCryptPasswordEncoder.encode("password"));
            userRepository.save(user);
            String dto = objectMapper.writeValueAsString(new LoginDTO("anotherUsername", "anotherPassword"));
            //When
            ResultActions jsonResp = mockMvc.perform(post("/api/v1/login").content(dto).contentType(MediaType.APPLICATION_JSON));
            //Then
            jsonResp.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("username and/or password invalid"));
    }
}