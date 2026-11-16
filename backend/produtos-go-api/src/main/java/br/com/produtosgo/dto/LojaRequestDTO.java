package br.com.produtosgo.dto;

import jakarta.validation.constraints.*;

public record LojaRequestDTO(

        @NotNull
        Integer categoriaId,

        @NotBlank
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank
        @Size(max = 20, message = "O documento deve ter no máximo 20 caracteres")
        String documento,

        @NotBlank
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco,

        @NotNull
        @DecimalMin(value = "-90.0", message = "Latitude inválida")
        @DecimalMax(value = "90.0", message = "Latitude inválida")
        Double latitude,

        @NotNull
        @DecimalMin(value = "-180.0", message = "Longitude inválida")
        @DecimalMax(value = "180.0", message = "Longitude inválida")
        Double longitude

) {}
