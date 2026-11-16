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
            SELECT l.id, l.nome, c.nome AS categoria,
                   ST_Y(l.localizacao::geometry) AS latitude,
                   ST_X(l.localizacao::geometry) AS longitude,
                   ST_Distance(
                       l.localizacao,
                       ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                   ) AS distancia_metros
              FROM lojas l
              JOIN categorias c ON c.id = l.categoria_id
             WHERE l.status = 'ACTIVE'
               AND ST_DWithin(
                   l.localizacao,
                   ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
                   ?
               )
               AND (CAST(? AS VARCHAR) IS NULL OR c.nome = ?)
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
        String sql = "SELECT loja_id, dia_semana, horario_abertura, horario_fechamento FROM lojas_horarios "
            + "WHERE loja_id IN (" + marcadores + ") "
            + "ORDER BY loja_id, dia_semana, horario_abertura";
        jdbcTemplate.query(sql, rs -> {
            porLoja.computeIfAbsent(rs.getLong("loja_id"), k -> new java.util.ArrayList<>())
                .add(new HorarioDia(
                    rs.getInt("dia_semana"),
                    rs.getObject("horario_abertura", LocalTime.class),
                    rs.getObject("horario_fechamento", LocalTime.class)));
        }, lojaIds.toArray());
        return porLoja;
    }
}
