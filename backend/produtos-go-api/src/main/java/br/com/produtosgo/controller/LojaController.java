package br.com.produtosgo.controller;

import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.service.LojaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
