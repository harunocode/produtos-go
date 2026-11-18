package br.com.produtosgo.service;

import br.com.produtosgo.dto.auth.LoginRequestDto;
import br.com.produtosgo.dto.auth.LoginResponseDto;
import br.com.produtosgo.dto.auth.RegisterRequestDto;
import br.com.produtosgo.entity.Users;
import br.com.produtosgo.enums.UserType;
import br.com.produtosgo.repository.UsersRepository;
import br.com.produtosgo.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    static final String TYPE_COSTUMER = "CUSTOMER";
    static final String TYPE_ESTABLISHMENT = "ESTABLISHMENT";
    private static final String TOKEN_TYPE = "Bearer";

    private final UsersRepository usersRepository;
    private final CategoriaRepository categoriaRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final Clock clock;

    @Transactional
    public LoginResponseDto register(RegisterRequestDto dto) {
        validateUserFields(dto);

        String normalizedEmail = dto.email().trim().toLowerCase();

        if (dto.type() == UserType.ESTABELECIMENTO) {
            validarDadosDaLoja(dto);
        }

        if (usersRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Usuário já cadastrado com este e-mail"
            );
        }

        Users user = new Users();

        user.setName(dto.name().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setType(dto.type());
        user.setCreated_at(
                Timestamp.from(Instant.now(clock))
        );

        Users savedUser = usersRepository.save(user);

        if (dto.type() == UserType.ESTABELECIMENTO) {
            criarLoja(dto, savedUser.getId());
        }

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
        if (rawPassword == null || !passwordEncoder.matches(rawPassword, storedPassword)) {
            throw invalidCredentials();
        }
    }

    private void validarDadosDaLoja(RegisterRequestDto dto) {
        String cnpj = somenteDigitos(dto.cnpj());
        String cep = somenteDigitos(dto.cep());
        if (dto.lojaNome() == null || dto.lojaNome().isBlank()) {
            throw new IllegalArgumentException("Nome da loja é obrigatório");
        }
        if (cnpj.length() != 14) {
            throw new IllegalArgumentException("O CNPJ deve conter 14 dígitos");
        }
        if (dto.categoriaId() == null || !categoriaRepository.existsById(dto.categoriaId())) {
            throw new IllegalArgumentException("Categoria da loja inválida");
        }
        if (cep.length() != 8) {
            throw new IllegalArgumentException("O CEP deve conter 8 dígitos");
        }
        if (dto.latitude() == null || dto.longitude() == null
                || !Double.isFinite(dto.latitude()) || !Double.isFinite(dto.longitude())
                || dto.latitude() < -90 || dto.latitude() > 90
                || dto.longitude() < -180 || dto.longitude() > 180) {
            throw new IllegalArgumentException("Coordenadas da loja inválidas");
        }
        Integer existentes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM lojas WHERE cnpj = ?", Integer.class, cnpj);
        if (existentes != null && existentes > 0) {
            throw new IllegalArgumentException("Já existe uma loja cadastrada com este CNPJ");
        }
    }

    private void criarLoja(RegisterRequestDto dto, Long userId) {
        Long lojaId = jdbcTemplate.queryForObject("""
                INSERT INTO lojas
                    (usuario_id, nome, cnpj, categoria_id, cep, status, localizacao)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE',
                    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)
                RETURNING id
                """, Long.class, userId, dto.lojaNome().trim(), somenteDigitos(dto.cnpj()),
                dto.categoriaId(), somenteDigitos(dto.cep()), dto.longitude(), dto.latitude());

        for (int dia = 1; dia <= 6; dia++) {
            LocalTime abre = LocalTime.of(8, 0);
            LocalTime fecha = dia == 6 ? LocalTime.of(12, 0) : LocalTime.of(18, 0);
            jdbcTemplate.update("""
                    INSERT INTO lojas_horarios
                        (loja_id, dia_semana, horario_abertura, horario_fechamento)
                    VALUES (?, ?, ?, ?)
                    """, lojaId, dia, abre, fecha);
        }
    }

    private static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
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
