package br.com.produtosgo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    protected Categoria() {}

    public Integer getId() { return id; }
}
