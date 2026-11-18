package br.com.produtosgo.dto.auth;

import br.com.produtosgo.enums.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @NotNull UserType type,
        @Size(max = 150) String lojaNome,
        String cnpj,
        @Positive Integer categoriaId,
        String cep,
        Double latitude,
        Double longitude
) {

    @Override
    public String toString() {
        return "RegisterRequestDto[name=%s, email=%s, ******".formatted( name, email);
    }
}
