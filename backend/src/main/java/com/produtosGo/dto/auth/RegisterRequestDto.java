package com.produtosGo.dto.auth;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotNull Long storeId,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull Role role,
        LocalDate startDate,
        @Size(max = 50) String currentState,
        Boolean inLearningPath,
        Boolean consent
) {
    public enum Role {
        EMPLOYEE,
        MANAGER
    }

    @Override
    public String toString() {
        return "RegisterRequestDto[storeId=%s, name=%s, email=%s, ******".formatted(storeId, name, email);
    }
}