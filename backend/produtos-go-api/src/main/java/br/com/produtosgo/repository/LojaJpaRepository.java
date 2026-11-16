package br.com.produtosgo.repository;

import br.com.produtosgo.entity.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojaJpaRepository extends JpaRepository<Loja, Long> {}
