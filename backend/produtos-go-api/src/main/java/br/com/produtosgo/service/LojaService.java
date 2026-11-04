package br.com.produtosgo.service;

import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.repository.LojaRepository;
import java.util.List;
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
        return repository.buscarProximas(
            latitude, longitude, raioMetros,
            StringUtils.hasText(categoria) ? categoria : null
        );
    }
}
