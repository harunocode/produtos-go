package com.produtosGo.controller;

import com.produtosGo.dto.auth.LoginRequestDto;
import com.produtosGo.dto.auth.LoginResponseDto;
import com.produtosGo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        // O controller recebe os dados do DTO, passa para o service processar
        LoginResponseDto response = authService.autenticar(request);

        // Retorna o JWT gerado com status 200 OK
        return ResponseEntity.ok(response);
    }
}

