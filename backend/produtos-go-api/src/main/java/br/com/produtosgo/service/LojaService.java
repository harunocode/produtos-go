package br.com.produtosgo.service;

import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.dto.LojaRequestDTO;
import br.com.produtosgo.entity.Loja;
import br.com.produtosgo.repository.LojaRepository;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LojaService {
    private final LojaRepository repository;

    public LojaService(LojaRepository repository) {
        this.repository = repository;
    }

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

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

    @Transactional
    public Loja criarLoja(LojaRequestDTO request) {

        if (request.latitude() < -90 || request.latitude() > 90) {
            throw new IllegalArgumentException("Latitude inválida.");
        }

        if (request.longitude() < -180 || request.longitude() > 180) {
            throw new IllegalArgumentException("Longitude inválida.");
        }

        Point localizacao = geometryFactory.createPoint(
                new Coordinate(
                        request.longitude(),
                        request.latitude()
                )
        );

        Loja loja = new Loja();

        loja.setCategoriaId(request.categoriaId());
        loja.setNome(request.nome());
        loja.setDocumento(request.documento());
        loja.setEndereco(request.endereco());
        loja.setLocalizacao(localizacao);
        loja.setStatus("ATIVO");
        loja.setCriadoEm(OffsetDateTime.now());
        loja.setUserId(userId);

        return repository.save(loja);
    }

}
