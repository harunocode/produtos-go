package br.com.produtosgo.controller;

import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.dto.LojaRequestDTO;
import br.com.produtosgo.service.LojaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lojas")
@Validated
public class LojaController {
    private final LojaService service;

    public LojaController(LojaService service) {
        this.service = service;
    }

    @GetMapping("/proximas")
    public List<LojaProximaResponse> buscarProximas(
        @RequestParam @Min(-90) @Max(90) double latitude,
        @RequestParam @Min(-180) @Max(180) double longitude,
        @RequestParam(defaultValue = "3000") @Min(100) @Max(100000) int raioMetros,
        @RequestParam(required = false) String categoria
    ) {
        return service.buscarProximas(latitude, longitude, raioMetros, categoria);
    }

    @PostMapping
    public LojaProximaResponse criarNovoLoja(@Valid @RequestBody LojaRequestDTO request){
        return service.criarLoja(request);
    }

}
