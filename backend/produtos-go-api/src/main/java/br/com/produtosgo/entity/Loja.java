package br.com.produtosgo.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "categoria_id", nullable = false)
    private Integer categoriaId;

    @Column(name = "nome")
    private String nome;

    @Column(name = "documento")
    private String documento;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "localizacao", columnDefinition = "geography(Point,4326)")
    private Point localizacao;

    @Column(name = "status")
    private String status;

    @Column(name = "criado_em")
    private OffsetDateTime criadoEm;

    @Column(name = "user_id")
    private Long userId;

    public Long getId() {
        return id;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEndereco() {
        return endereco;
    }

    public Point getLocalizacao() {
        return localizacao;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setLocalizacao(Point localizacao) {
        this.localizacao = localizacao;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
