package br.com.produtosgo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank @Size(max = 120) String nome,
    @NotNull @DecimalMin("0.00") BigDecimal preco,
    @NotNull @Min(0) Integer estoque,
    @Size(max = 500) String descricao,
    Boolean ativo
) {}
