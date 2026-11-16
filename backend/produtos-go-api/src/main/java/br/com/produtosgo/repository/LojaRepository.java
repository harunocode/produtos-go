package br.com.produtosgo.repository;

import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.entity.Loja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LojaRepository extends JpaRepository<Loja, Long> {

    @Query(value = """
        SELECT
            id,
            nome,
            categoria_id,
            ST_Y(localizacao::geometry) AS latitude,
            ST_X(localizacao::geometry) AS longitude,
            ST_Distance(
                localizacao,
                ST_SetSRID(
                    ST_MakePoint(:longitude, :latitude),
                    4326
                )::geography
            ) AS distancia_metros
        FROM lojas
        WHERE localizacao IS NOT NULL
          AND status = 'ATIVO'
          AND ST_DWithin(
              localizacao,
              ST_SetSRID(
                  ST_MakePoint(:longitude, :latitude),
                  4326
              )::geography,
              :raioMetros
          )
          AND (
              CAST(:categoria AS VARCHAR) IS NULL
              OR categoria = :categoria
          )
        ORDER BY distancia_metros
        """, nativeQuery = true)
    List<LojaProximaResponse> buscarProximas(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("raioMetros") int raioMetros,
            @Param("categoria") String categoria
    );
}