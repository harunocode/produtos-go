package com.produtosGo.services;

import com.produtosGo.dto.auth.LoginRequestDto;
import com.produtosGo.dto.auth.LoginResponseDto;
import com.produtosGo.dto.auth.RegisterRequestDto;
import com.produtosGo.enums.TypeUsers;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;

@AllArgsConstructor
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    static final String TYPE_COSTUMER = "CUSTOMER";
    static final String TYPE_ESTABLISHMENT = "ESTABLISHMENT";
    private static final String TOKEN_TYPE = "Bearer";

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    private final String dummyHash;

    @Transactional
    public LoginResponseDto register(RegisterRequestDto dto) {

        return null;
    }

    public LoginResponseDto login(LoginRequestDto dto) {

        return null;
    }

    private void checkPassword(String rawPassword, String encodedPassword) {

    }

    private LoginResponseDto buildResponse(String id, String email, TypeUsers type) {
        String token = jwtService.generateToken(id, email, String.valueOf(type));
        return new LoginResponseDto(token, AuthService.TOKEN_TYPE, id, email, type);
    }

    private static void validateEmployeeFields(RegisterRequestDto dto) {

    }

    private static BadCredentialsException invalidCredentials() {

        return null;
    }
}