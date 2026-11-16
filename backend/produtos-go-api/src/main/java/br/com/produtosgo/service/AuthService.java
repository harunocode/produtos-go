package br.com.produtosgo.service;

import br.com.produtosgo.dto.auth.LoginRequestDto;
import br.com.produtosgo.dto.auth.LoginResponseDto;
import br.com.produtosgo.dto.auth.RegisterRequestDto;
import br.com.produtosgo.entity.Users;
import br.com.produtosgo.enums.UserType;
import br.com.produtosgo.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    static final String TYPE_COSTUMER = "CUSTOMER";
    static final String TYPE_ESTABLISHMENT = "ESTABLISHMENT";
    private static final String TOKEN_TYPE = "Bearer";

    private final UsersRepository usersRepository;

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
        user.setPassword(dto.password());
        user.setType(dto.type());
        user.setCreated_at(
                Timestamp.from(Instant.now(clock))
        );

        Users savedUser = usersRepository.save(user);

        log.info(
                "Usuário registrado com sucesso: {}",
                savedUser.getEmail()
        );

        return buildResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getType()
        );
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        String normalizedEmail = dto.email().trim().toLowerCase();
        Users user = usersRepository.findByEmail(normalizedEmail)
                .orElseThrow(AuthService::invalidCredentials);

        checkPassword(dto.password(), user.getPassword());

        return buildResponse(user.getId(), user.getEmail(), user.getType());
    }

    private void checkPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || !rawPassword.equals(storedPassword)) {
            throw invalidCredentials();
        }
    }

    private LoginResponseDto buildResponse(Long id, String email, UserType type) {
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

        if (dto.password() == null || dto.password().length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres");
        }

        if (dto.type() == null) {
            throw new IllegalArgumentException("Tipo de usuário é obrigatório");
        }
    }

    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
    }
}