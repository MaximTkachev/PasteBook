package com.qwerty.pastebook.controllers;

import com.qwerty.pastebook.dto.auth.LoginDTO;
import com.qwerty.pastebook.dto.auth.RegisterDTO;
import com.qwerty.pastebook.dto.auth.TokenDTO;
import com.qwerty.pastebook.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public TokenDTO register(@RequestBody @Valid RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody @Valid LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }
}
