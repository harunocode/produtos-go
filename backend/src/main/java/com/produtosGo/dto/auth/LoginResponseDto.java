package com.produtosGo.dto.auth;

public record LoginResponseDto(
        String token,
        String tokenType,
        String userId,
        String email,
        String role
) {}