package br.com.produtosgo.service;

import br.com.produtosgo.dto.HorarioDia;
import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.repository.LojaRepository;
import br.com.produtosgo.repository.LojaRepository.LojaProximaRow;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LojaService {
    private final LojaRepository repository;

    public LojaService(LojaRepository repository) {
        this.repository = repository;
    }

    public List<LojaProximaResponse> buscarProximas(
        double latitude, double longitude, int raioMetros, String categoria
    ) {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Latitude inválida.");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Longitude inválida.");
        if (raioMetros < 100 || raioMetros > 100000) {
            throw new IllegalArgumentException("O raio deve estar entre 100 e 100000 metros.");
        }
        List<LojaProximaRow> lojas = repository.buscarProximas(
            latitude, longitude, raioMetros,
            StringUtils.hasText(categoria) ? categoria : null
        );
        Map<Long, List<HorarioDia>> horarios = repository.buscarHorarios(
            lojas.stream().map(LojaProximaRow::id).toList()
        );
        ZonedDateTime agora = ZonedDateTime.now(HorarioFuncionamento.FUSO);

        return lojas.stream().map(l -> {
            List<HorarioDia> deLoja = horarios.getOrDefault(l.id(), List.of());
            HorarioFuncionamento.Status st = HorarioFuncionamento.calcular(deLoja, agora);
            return new LojaProximaResponse(
                l.id(), l.nome(), l.categoria(), l.latitude(), l.longitude(), l.distanciaMetros(),
                st.aberto(), st.texto(), st.horarioHoje(), deLoja
            );
        }).toList();
    }
}
