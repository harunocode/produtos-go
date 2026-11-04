package com.produtosGo.services;

import com.produtosGo.dto.auth.LoginRequestDto;
import com.produtosGo.dto.auth.RegisterRequestDto;
import com.produtosGo.entities.Users;
import com.produtosGo.enums.UserType;
import com.produtosGo.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void register_shouldPersistUserAndReturnToken() {
        var repository = new FakeUsersRepository();
        var jwtService = new JwtService();
        jwtService.setSecret("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        jwtService.setExpiration(86_400_000L);

        var service = new AuthService(repository, passwordEncoder, jwtService, fixedClock);

        var response = service.register(new RegisterRequestDto("Maria", "maria@email.com", "senha123", UserType.COSTUMER));

        assertNotNull(response);
        assertEquals("maria@email.com", response.email());
        assertEquals(UserType.COSTUMER, response.type());
        assertNotNull(response.token());
        assertEquals("Bearer", response.tokenType());
        assertTrue(passwordEncoder.matches("senha123", repository.findByEmail("maria@email.com").orElseThrow().getPassword_hash()));
    }

    @Test
    void login_shouldValidateCredentialsAndReturnToken() {
        var repository = new FakeUsersRepository();
        var jwtService = new JwtService();
        jwtService.setSecret("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        jwtService.setExpiration(86_400_000L);

        var user = new Users();
        user.setId(UUID.randomUUID());
        user.setName("Joao");
        user.setEmail("joao@email.com");
        user.setPassword_hash(passwordEncoder.encode("senha123"));
        user.setType(UserType.ESTABLISHMENT);
        user.setActive(true);
        repository.save(user);

        var service = new AuthService(repository, passwordEncoder, jwtService, fixedClock);

        var response = service.login(new LoginRequestDto("joao@email.com", "senha123"));

        assertNotNull(response);
        assertEquals("joao@email.com", response.email());
        assertEquals(UserType.ESTABLISHMENT, response.type());
        assertNotNull(response.token());
    }

    private static final class FakeUsersRepository implements UsersRepository {
        private final java.util.Map<String, Users> users = new java.util.HashMap<>();

        @Override
        public Optional<Users> findByEmail(String email) {
            return Optional.ofNullable(users.get(email.toLowerCase()));
        }

        @Override
        public boolean existsByEmail(String email) {
            return users.containsKey(email.toLowerCase());
        }

        @Override
        public Users save(Users user) {
            user.setEmail(user.getEmail().toLowerCase());
            users.put(user.getEmail(), user);
            return user;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Users> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Users> List<S> saveAllAndFlush(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public void deleteAllInBatch(Iterable<Users> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Users getOne(UUID uuid) {
            return null;
        }

        @Override
        public Users getById(UUID uuid) {
            return null;
        }

        @Override
        public Users getReferenceById(UUID uuid) {
            return null;
        }

        @Override
        public <S extends Users> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Users> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends Users> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends Users> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Users> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Users> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Users, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public <S extends Users> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public Optional<Users> findById(UUID uuid) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        @Override
        public List<Users> findAll() {
            return List.of();
        }

        @Override
        public List<Users> findAllById(Iterable<UUID> uuids) {
            return List.of();
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(UUID uuid) {

        }

        @Override
        public void delete(Users entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends Users> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Users> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<Users> findAll(Pageable pageable) {
            return null;
        }
    }
}
