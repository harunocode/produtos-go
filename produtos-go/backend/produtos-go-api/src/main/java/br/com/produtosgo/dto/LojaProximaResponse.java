package br.com.produtosgo.dto;

import java.util.List;

public record LojaProximaResponse(
    Long id, String nome, String categoria,
    Double latitude, Double longitude, Double distanciaMetros,
    boolean aberto,          // aberta neste momento (fuso de Brasília)
    String statusHorario,    // ex.: "Aberto · fecha às 18h" ou "Fechado · abre amanhã às 8h"
    String horarioHoje,      // ex.: "8h–12h e 13h30–18h"; "Fechado" se não abre hoje; null se não há horários
    List<HorarioDia> horarios
) {}
