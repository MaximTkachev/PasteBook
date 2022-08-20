package com.qwerty.pastebook.services;

import com.qwerty.pastebook.dto.auth.LoginDTO;
import com.qwerty.pastebook.dto.auth.RegisterDTO;
import com.qwerty.pastebook.dto.auth.TokenDTO;
import com.qwerty.pastebook.entities.UserEntity;
import com.qwerty.pastebook.exceptions.BadRequestException;
import com.qwerty.pastebook.exceptions.ServerError;
import com.qwerty.pastebook.repositories.UserRepository;
import com.qwerty.pastebook.security.JWTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JWTokenProvider jwTokenProvider;

    @Transactional
    public TokenDTO register(RegisterDTO dto) {
        String hashedPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        UserEntity user = new UserEntity(dto.getUsername(), hashedPassword);
        try {
            userRepository.save(user);
            return jwTokenProvider.createTokenForUser(user);
        } catch (Exception e) {
            if (e.getClass().equals(DataIntegrityViolationException.class))
                throw new BadRequestException("username has already taken");
            else {
                throw new ServerError();
            }
        }
    }

    @Transactional(readOnly = true)
    public TokenDTO login(LoginDTO dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user doesn't exist"));
        return jwTokenProvider.createTokenForUser(user);
    }
}
