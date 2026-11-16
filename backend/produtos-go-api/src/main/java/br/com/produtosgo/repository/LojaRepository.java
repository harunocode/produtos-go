package br.com.produtosgo.repository;

import br.com.produtosgo.dto.HorarioDia;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** SQL espacial. GEOGRAPHY permite expressar distâncias em metros. */
@Repository
public class LojaRepository {
    /** Linha da busca espacial, ainda sem os horários. */
    public record LojaProximaRow(
        Long id, String nome, String categoria,
        Double latitude, Double longitude, Double distanciaMetros
    ) {}

    private final JdbcTemplate jdbcTemplate;

    public LojaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LojaProximaRow> buscarProximas(
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
        return jdbcTemplate.query(sql, (rs, row) -> new LojaProximaRow(
            rs.getLong("id"), rs.getString("nome"), rs.getString("categoria"),
            rs.getDouble("latitude"), rs.getDouble("longitude"),
            rs.getDouble("distancia_metros")
        ), longitude, latitude, longitude, latitude, raioMetros, categoria, categoria);
    }

    /** Horários de várias lojas em uma única consulta (evita uma query por loja). */
    public Map<Long, List<HorarioDia>> buscarHorarios(Collection<Long> lojaIds) {
        Map<Long, List<HorarioDia>> porLoja = new HashMap<>();
        if (lojaIds.isEmpty()) return porLoja;

        String marcadores = String.join(",", java.util.Collections.nCopies(lojaIds.size(), "?"));
        String sql = "SELECT loja_id, dia_semana, abre, fecha FROM lojas_horarios "
            + "WHERE loja_id IN (" + marcadores + ") ORDER BY loja_id, dia_semana, abre";
        jdbcTemplate.query(sql, rs -> {
            porLoja.computeIfAbsent(rs.getLong("loja_id"), k -> new java.util.ArrayList<>())
                .add(new HorarioDia(
                    rs.getInt("dia_semana"),
                    rs.getObject("abre", LocalTime.class),
                    rs.getObject("fecha", LocalTime.class)));
        }, lojaIds.toArray());
        return porLoja;
    }
}
