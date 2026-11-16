package br.com.produtosgo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

/** Um intervalo de funcionamento. diaSemana segue a ISO-8601: 1 = segunda ... 7 = domingo. */
public record HorarioDia(
    int diaSemana,
    @JsonFormat(pattern = "HH:mm") LocalTime abre,
    @JsonFormat(pattern = "HH:mm") LocalTime fecha
) {}
