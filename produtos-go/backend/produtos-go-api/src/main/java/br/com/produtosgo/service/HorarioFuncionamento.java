package br.com.produtosgo.service;

import br.com.produtosgo.dto.HorarioDia;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de "aberto agora". Usa sempre o fuso de Brasília, independente do servidor.
 * Limitação: não trata horários que viram a madrugada.
 */
public final class HorarioFuncionamento {
    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final String[] DIAS_CURTOS = {"seg", "ter", "qua", "qui", "sex", "sáb", "dom"};

    public record Status(boolean aberto, String texto, String horarioHoje) {}

    private HorarioFuncionamento() {}

    public static Status calcular(List<HorarioDia> horarios, ZonedDateTime momento) {
        if (horarios == null || horarios.isEmpty()) {
            return new Status(false, "Horário não informado", null);
        }
        ZonedDateTime agora = momento.withZoneSameInstant(FUSO);
        LocalTime hora = agora.toLocalTime();
        int hoje = agora.getDayOfWeek().getValue();

        List<HorarioDia> deHoje = doDia(horarios, hoje);
        String resumoHoje = deHoje.isEmpty() ? "Fechado" : deHoje.stream()
            .map(h -> formatar(h.abre()) + "–" + formatar(h.fecha()))
            .collect(Collectors.joining(" e "));

        for (HorarioDia h : deHoje) {
            if (!hora.isBefore(h.abre()) && hora.isBefore(h.fecha())) {
                return new Status(true, "Aberto · fecha às " + formatar(h.fecha()), resumoHoje);
            }
        }
        for (HorarioDia h : deHoje) {
            if (h.abre().isAfter(hora)) {
                return new Status(false, "Fechado · abre às " + formatar(h.abre()), resumoHoje);
            }
        }
        for (int i = 1; i <= 7; i++) {
            int dia = (hoje - 1 + i) % 7 + 1;
            List<HorarioDia> lista = doDia(horarios, dia);
            if (!lista.isEmpty()) {
                String quando = i == 1 ? "amanhã" : DIAS_CURTOS[dia - 1];
                return new Status(false, "Fechado · abre " + quando + " às " + formatar(lista.get(0).abre()), resumoHoje);
            }
        }
        return new Status(false, "Fechado", resumoHoje);
    }

    private static List<HorarioDia> doDia(List<HorarioDia> horarios, int dia) {
        return horarios.stream()
            .filter(h -> h.diaSemana() == dia)
            .sorted(Comparator.comparing(HorarioDia::abre))
            .toList();
    }

    /** 08:00 -> "8h"; 13:30 -> "13h30". */
    static String formatar(LocalTime t) {
        return t.getHour() + "h" + (t.getMinute() == 0 ? "" : String.format("%02d", t.getMinute()));
    }
}
