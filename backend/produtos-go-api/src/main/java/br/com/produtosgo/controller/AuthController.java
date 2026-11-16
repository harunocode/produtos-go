package br.com.produtosgo.controller;

import br.com.produtosgo.dto.auth.LoginRequestDto;
import br.com.produtosgo.dto.auth.LoginResponseDto;
import br.com.produtosgo.dto.auth.RegisterRequestDto;
import br.com.produtosgo.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }
}


