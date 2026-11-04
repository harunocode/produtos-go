package com.produtosGo.dto.auth;

import com.produtosGo.enums.TypeUsers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password_hash,
        @NotNull TypeUsers type
) {

    @Override
    public String toString() {
        return "RegisterRequestDto[name=%s, email=%s, ******".formatted( name, email);
    }
}