package com.produtosGo.services;

import com.produtosGo.dto.auth.LoginRequestDto;
import com.produtosGo.dto.auth.LoginResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    //falta inserir os users
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto dto) {

        //users

//        if (customer.isPresent() && seller.isPresent()) {
//            throw new IllegalStateException(
//                    "Email duplicado entre funcionários e gerentes"
//            );
//        }
//
//        if (customer.isPresent()) {
//
//            CustomerEntity user = customer.get();
//
//            validatePassword(dto.password(), user.getPassword());
//
//            return createResponse(
//                    user.getId().toString(),
//                    user.getEmail(),
//                        "Customer"
//            );
//        return null;
//    }
//
//        if (seller.isPresent()) {
//
//            SellerEntity user = seller.get();
//
//            validatePassword(dto.password(), user.getPassword());
//
//            return createResponse(
//                    user.getId().toString(),
//                    user.getEmail(),
//                    "Seller"
//            );
//        }
//
//        throw new BadCredentialsException(
//                "Email ou senha inválidos"
//        );
        return null;
    }

    private void validatePassword(
            String rawPassword,
            String encodedPassword) {

        if (!passwordEncoder.matches(
                rawPassword,
                encodedPassword)) {

            throw new BadCredentialsException(
                    "Email ou senha inválidos"
            );
        }
    }

    private LoginResponseDto createResponse(
            String id,
            String email,
            String role) {

        String token = jwtService.generateToken(
                id,
                email,
                role
        );

        return new LoginResponseDto(
                token,
                "Bearer",
                id,
                email,
                role
        );
    }

    public LoginResponseDto autenticar(LoginRequestDto request) {
//        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                request.getUsername(),
//                request.getPassword()
//        );
//
//        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        String token = jwtService.generateToken(userDetails);
//
//        return new LoginResponseDto(token);
        return null;
    }
}