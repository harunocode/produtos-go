package br.com.produtosgo.dto.auth;

import br.com.produtosgo.enums.UserType;

public record LoginResponseDto(
        String token,
        String tokenType,
        Long userId,
        String email,
        UserType type
) {}