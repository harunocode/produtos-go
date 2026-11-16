package br.com.produtosgo.repository;

import br.com.produtosgo.entity.Produto;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();

    @Query("""
        SELECT p FROM Produto p
        JOIN FETCH p.loja
        JOIN FETCH p.categoria
        WHERE p.loja.id IN :lojaIds
          AND p.ativo = TRUE
          AND p.estoque > 0
        ORDER BY p.loja.id, p.nome
        """)
    List<Produto> buscarDisponiveisNasLojas(@Param("lojaIds") Collection<Long> lojaIds);
}
