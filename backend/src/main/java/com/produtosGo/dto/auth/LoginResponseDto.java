package com.produtosGo.dto.auth;

import com.produtosGo.enums.UserType;

public record LoginResponseDto(
        String token,
        String tokenType,
        String userId,
        String email,
        UserType type
) {}