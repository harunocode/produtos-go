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

    protected Loja() {}

    public Long getId() { return id; }
}
