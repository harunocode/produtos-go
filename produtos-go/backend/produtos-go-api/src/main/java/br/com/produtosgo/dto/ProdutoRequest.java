package br.com.produtosgo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank @Size(max = 200) String nome,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal preco,
    @NotNull @Min(0) Integer estoque,
    @Size(max = 500) String descricao,
    Boolean ativo,
    @NotNull @Positive Integer categoriaId,
    @NotNull @Positive Long lojaId
) {}
