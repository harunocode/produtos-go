package br.com.produtosgo.dto;

import java.math.BigDecimal;

public record ProdutoProximoResponse(
    Long id,
    String nome,
    BigDecimal preco,
    Integer estoque,
    String descricao,
    Integer categoriaId,
    LojaProximaResponse loja
) {}
