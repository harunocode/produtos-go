package br.com.produtosgo.repository;

import br.com.produtosgo.dto.LojaProximaResponse;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** SQL espacial. GEOGRAPHY permite expressar distâncias em metros. */
@Repository
public class LojaRepository {
    private final JdbcTemplate jdbcTemplate;

    public LojaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LojaProximaResponse> buscarProximas(
        double latitude, double longitude, int raioMetros, String categoria
    ) {
        String sql = """
            SELECT id, nome, categoria,
                   ST_Y(localizacao::geometry) AS latitude,
                   ST_X(localizacao::geometry) AS longitude,
                   ST_Distance(
                       localizacao,
                       ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                   ) AS distancia_metros
              FROM lojas
             WHERE ativa = TRUE
               AND ST_DWithin(
                   localizacao,
                   ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
                   ?
               )
               AND (CAST(? AS VARCHAR) IS NULL OR categoria = ?)
             ORDER BY distancia_metros
            """;
        return jdbcTemplate.query(sql, (rs, row) -> new LojaProximaResponse(
            rs.getLong("id"), rs.getString("nome"), rs.getString("categoria"),
            rs.getDouble("latitude"), rs.getDouble("longitude"),
            rs.getDouble("distancia_metros")
        ), longitude, latitude, longitude, latitude, raioMetros, categoria, categoria);
    }
}
