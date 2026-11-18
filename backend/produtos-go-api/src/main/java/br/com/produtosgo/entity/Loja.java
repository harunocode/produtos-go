package br.com.produtosgo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lojas")
public class Loja {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "categoria_id", nullable = false)
    private Integer categoriaId;

    protected Loja() {}

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Integer getCategoriaId() { return categoriaId; }
}
