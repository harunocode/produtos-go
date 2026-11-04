package com.produtosGo.dto.auth;

import com.produtosGo.enums.TypeUsers;

public record LoginResponseDto(
        String token,
        String tokenType,
        String userId,
        String email,
        TypeUsers type
) {}