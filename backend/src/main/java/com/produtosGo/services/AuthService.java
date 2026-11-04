package com.produtosGo.services;

import com.produtosGo.dto.auth.LoginRequestDto;
import com.produtosGo.dto.auth.LoginResponseDto;
import com.produtosGo.dto.auth.RegisterRequestDto;
import com.produtosGo.entities.Users;
import com.produtosGo.enums.UserType;
import com.produtosGo.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    static final String TYPE_COSTUMER = "CUSTOMER";
    static final String TYPE_ESTABLISHMENT = "ESTABLISHMENT";
    private static final String TOKEN_TYPE = "Bearer";

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    @Transactional
    public LoginResponseDto register(RegisterRequestDto dto) {
        validateUserFields(dto);

        String normalizedEmail = dto.email().trim().toLowerCase();

        if (usersRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Usuário já cadastrado com este e-mail"
            );
        }

        Users user = new Users();

        user.setName(dto.name().trim());
        user.setEmail(normalizedEmail);
        user.setPassword_hash(
                passwordEncoder.encode(dto.password_hash())
        );
        user.setType(dto.type());
        user.setActive(true);
        user.setCreated_at(
                Timestamp.from(Instant.now(clock))
        );

        Users savedUser = usersRepository.save(user);

        log.info(
                "Usuário registrado com sucesso: {}",
                savedUser.getEmail()
        );

        return buildResponse(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getType()
        );
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        String normalizedEmail = dto.email().trim().toLowerCase();
        Users user = usersRepository.findByEmail(normalizedEmail)
                .orElseThrow(AuthService::invalidCredentials);

        checkPassword(dto.password(), user.getPassword_hash());

        if (!user.isActive()) {
            throw invalidCredentials();
        }

        return buildResponse(user.getId().toString(), user.getEmail(), user.getType());
    }

    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw invalidCredentials();
        }
    }

    private LoginResponseDto buildResponse(String id, String email, UserType type) {
        String token = jwtService.generateToken(id, email, String.valueOf(type));
        return new LoginResponseDto(token, TOKEN_TYPE, id, email, type);
    }

    private static void validateUserFields(RegisterRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados do usuário não informados");
        }

        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }

        if (dto.password_hash() == null || dto.password_hash().length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres");
        }

        if (dto.type() == null) {
            throw new IllegalArgumentException("Tipo de usuário é obrigatório");
        }
    }

    private static BadCredentialsException invalidCredentials() {
        return new BadCredentialsException("E-mail ou senha inválidos");
    }
}