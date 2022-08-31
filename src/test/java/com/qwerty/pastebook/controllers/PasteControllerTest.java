package com.qwerty.pastebook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwerty.pastebook.dto.auth.RegisterDTO;
import com.qwerty.pastebook.dto.auth.TokenDTO;
import com.qwerty.pastebook.dto.pastes.ExpirationPeriod;
import com.qwerty.pastebook.dto.pastes.HashDTO;
import com.qwerty.pastebook.dto.pastes.UploadPasteDTO;
import com.qwerty.pastebook.entities.AccessModifier;
import com.qwerty.pastebook.entities.UserEntity;
import com.qwerty.pastebook.repositories.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PasteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final String Authorization = "Authorization";

    private final String Bearer = "Bearer ";

    @DisplayName("Unsuccessful paste upload without token")
    @SneakyThrows
    @Test
    public void uploadPasteWithoutToken() {
        //Before
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", "text", AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON));
        //Then
        jsonResp.andExpect(status().isForbidden());
    }

    @DisplayName("Unsuccessful paste upload with expired token")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithExpiredToken() {
        //Before
        String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTUwNjAwfQ.JyPXHgkllYd5obvu3nnFDbprmnOCo_PtU_aT9HVGV-U";
        UserEntity userEntity = new UserEntity("username", "password");
        userRepository.save(userEntity);
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", "text", AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + expiredToken));
        //Then
        jsonResp.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("invalid token"));
    }

    @DisplayName("Unsuccessful paste upload with invalid token")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithInvalidToken() {
        //Before
        String invalidToken = "someStringThatIsNotAToken";
        UserEntity userEntity = new UserEntity("username", "password");
        userRepository.save(userEntity);
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", "text", AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + invalidToken));
        //Then
        jsonResp.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("invalid token"));
    }

    @DisplayName("Unsuccessful paste upload with null text")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithNullText() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", null, AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("text cannot be null"));
    }

    @DisplayName("Unsuccessful paste upload with null access modifier")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithNullAccessModifier() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", "some text", null, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("access modifier cannot be null"));
    }

    @DisplayName("Unsuccessful paste upload with invalid access modifier")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithInvalidAccessModifier() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = "{\"title\":\"title\",\"text\":\"some text\",\"accessModifier\":\"PROTECTED\",\"expirationPeriod\":\"Eternal\"}";
        System.out.println(dto);
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("some data is strange"));
    }

    @DisplayName("Unsuccessful paste upload with null expirationPeriod")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithNullExpirationPeriod() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("title", "some text", AccessModifier.PUBLIC, null));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("expiration period cannot be null"));
    }

    @DisplayName("Unsuccessful paste upload with invalid expiration period")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithInvalidExpirationPeriod() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = "{\"title\":\"title\",\"text\":\"some text\",\"accessModifier\":\"PRIVATE\",\"expirationPeriod\":\"1m\"}";
        System.out.println(dto);
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("some data is strange"));
    }

    @DisplayName("Successful paste upload with null title")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithNullTitle() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO(null, "some text", AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        System.out.println(dto);
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash").isString());
    }

    @DisplayName("Successful paste upload")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void uploadPasteWithAllFields() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("some title", "some text", AccessModifier.PUBLIC, ExpirationPeriod.Eternal));
        //When
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getToken()));
        //Then
        jsonResp.andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash").isString());
    }

    private TokenDTO registerNewTestUserAndGetValidToken() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("username", "password");
        String dto = objectMapper.writeValueAsString(registerDTO);
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));

        String content = jsonResp.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, TokenDTO.class);
    }

    private HashDTO uploadNewPasteForUser(TokenDTO tokenOfAuthor,
                                          AccessModifier accessModifier,
                                          ExpirationPeriod expirationPeriod) throws Exception {
        String dto = objectMapper.writeValueAsString(new UploadPasteDTO("some title", "some text", accessModifier, expirationPeriod));
        ResultActions jsonResp = mockMvc.perform(post("/api/v1/pastes/new").content(dto).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenOfAuthor.getToken()));
        String content = jsonResp.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, HashDTO.class);
    }

    @DisplayName("Unsuccessful getting of the missing paste")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getMissingPasteByHash() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        String hash = UUID.randomUUID().toString();
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hash)
                .header(Authorization, Bearer + token.getToken()));
        //Then
        jsonResp.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("paste not found"));
    }

    @DisplayName("Successful getting of paste by hash")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getPasteByHash() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        HashDTO hashDTO = uploadNewPasteForUser(token, AccessModifier.PUBLIC, ExpirationPeriod.OneDay);
        String rightDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtils.addDays(getCurrentUtcTime(), 1));
        rightDate = "^" + rightDate.substring(0, rightDate.length() - 1) + "\\d$";
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hashDTO.getHash())
                .header(Authorization, Bearer + token.getToken()));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("some title"))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.expiration", matchesPattern(rightDate)))
                .andExpect(jsonPath("$.accessModifier").value("PUBLIC"));
    }

    @DisplayName("Successful getting of my private paste")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getMyPrivatePaste() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        HashDTO hashDTO = uploadNewPasteForUser(token, AccessModifier.PRIVATE, ExpirationPeriod.OneHour);
        String rightDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtils.addHours(getCurrentUtcTime(), 1));
        rightDate = "^" + rightDate.substring(0, rightDate.length() - 1) + "\\d$";
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hashDTO.getHash())
                .header(Authorization, Bearer + token.getToken()));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("some title"))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.expiration", matchesPattern(rightDate)))
                .andExpect(jsonPath("$.accessModifier").value("PRIVATE"));
    }

    @DisplayName("Unsuccessful getting of someone else's private paste")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getSomeonePrivatePaste() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        HashDTO hashDTO = uploadNewPasteForUser(token, AccessModifier.PRIVATE, ExpirationPeriod.OneWeek);

        String dto = objectMapper.writeValueAsString(new RegisterDTO("username2", "password"));
        ResultActions jsonRespOfRegister = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        TokenDTO tokenDTO2 = objectMapper.readValue(jsonRespOfRegister.andReturn().getResponse().getContentAsString(), TokenDTO.class);
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hashDTO.getHash())
                .header(Authorization, Bearer + tokenDTO2.getToken()));
        //Then
        jsonResp.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("paste not found"));
    }

    private Date getCurrentUtcTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTS"));
        Date date = null;
        try {
            date = ldf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
           log.error("Error creating utc time in getCurrentUtcTime method");
        }
        return date;
    }

    @DisplayName("Successful getting of my unlisted paste")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getMyUnlisted() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        HashDTO hashDTO = uploadNewPasteForUser(token, AccessModifier.UNLISTED, ExpirationPeriod.TenMinutes);
        String rightDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtils.addMinutes(getCurrentUtcTime(), 10));
        rightDate = "^" + rightDate.substring(0, rightDate.length() - 1) + "\\d$";
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hashDTO.getHash())
                .header(Authorization, Bearer + token.getToken()));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("some title"))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.expiration", matchesPattern(rightDate)))
                .andExpect(jsonPath("$.accessModifier").value("UNLISTED"));
        //Then
    }

    @DisplayName("Successful getting of someone else's unlisted paste")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @SneakyThrows
    @Test
    public void getSomeoneUnlistedPaste() {
        //Before
        TokenDTO token = registerNewTestUserAndGetValidToken();
        HashDTO hashDTO = uploadNewPasteForUser(token, AccessModifier.UNLISTED, ExpirationPeriod.ThreeHours);

        String dto = objectMapper.writeValueAsString(new RegisterDTO("username2", "password"));
        ResultActions jsonRespOfRegister = mockMvc.perform(post("/api/v1/register").content(dto).contentType(MediaType.APPLICATION_JSON));
        TokenDTO tokenDTO2 = objectMapper.readValue(jsonRespOfRegister.andReturn().getResponse().getContentAsString(), TokenDTO.class);

        String rightDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtils.addHours(getCurrentUtcTime(), 3));
        rightDate = "^" + rightDate.substring(0, rightDate.length() - 1) + "\\d$";
        //When
        ResultActions jsonResp = mockMvc.perform(get("/api/v1/pastes/hash/" + hashDTO.getHash())
                .header(Authorization, Bearer + tokenDTO2.getToken()));
        //Then
        jsonResp.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("some title"))
                .andExpect(jsonPath("$.text").value("some text"))
                .andExpect(jsonPath("$.expiration", matchesPattern(rightDate)))
                .andExpect(jsonPath("$.accessModifier").value("UNLISTED"));
    }
}