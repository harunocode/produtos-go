package com.produtosGo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    static final String ROLE_EMPLOYEE = "EMPLOYEE";
    static final String ROLE_MANAGER = "MANAGER";
    private static final String TOKEN_TYPE = "Bearer";


//    private final StoreRepository storeRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final WorkerEmailValidator emailValidator;
//    private final Clock clock;

    /**
     * Hash descartável: quando o e-mail não existe ainda comparamos a senha com ele, para que
     * "e-mail inexistente" e "senha errada" levem o mesmo tempo (evita enumeração de usuários por timing).
     */
//    private final String dummyHash;
//
//    public AuthService(
//
//    }
//
//    @Transactional
//    public LoginResponseDto register(RegisterRequestDto dto) {
//
//    }
//
//    public LoginResponseDto login(LoginRequestDto dto) {
//
//    }
//
//    private void checkPassword(String rawPassword, String encodedPassword) {
//
//    }
//
//    private LoginResponseDto buildResponse(String id, String email, String role) {
//        String token = jwtService.generateToken(id, email, role);
//        return new LoginResponseDto(token, TOKEN_TYPE, id, email, role);
//    }
//
//    private static void validateEmployeeFields(RegisterRequestDto dto) {
//
//    }
//
//    private static BadCredentialsException invalidCredentials() {
//
//    }
}