package br.com.produtosgo.dto;

import br.com.produtosgo.entity.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, BigDecimal preco,
                              Integer estoque, String descricao, Boolean ativo,
                              Long lojaId, Integer categoriaId) {
    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(),
                p.getEstoque(), p.getDescricao(), p.getAtivo(),
                p.getLoja().getId(), p.getCategoria().getId());
    }
}
