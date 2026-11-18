package br.com.produtosgo.controller;

import br.com.produtosgo.dto.ProdutoRequest;
import br.com.produtosgo.dto.ProdutoResponse;
import br.com.produtosgo.dto.ProdutoProximoResponse;
import br.com.produtosgo.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) { this.service = service; }

    @GetMapping
    public List<ProdutoResponse> listar() { return service.listar(); }

    @GetMapping("/minha-loja")
    public List<ProdutoResponse> listarMinhaLoja(@AuthenticationPrincipal String email) {
        return service.listarMinhaLoja(email);
    }

    @GetMapping("/proximos")
    public List<ProdutoProximoResponse> buscarProximos(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam(defaultValue = "3000") int raioMetros,
        @RequestParam(required = false) String categoria
    ) {
        return service.buscarProximos(latitude, longitude, raioMetros, categoria);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) { return service.buscarPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@AuthenticationPrincipal String email,
                                 @Valid @RequestBody ProdutoRequest request) {
        return service.criar(email, request);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@AuthenticationPrincipal String email,
                                     @PathVariable Long id,
                                     @Valid @RequestBody ProdutoRequest request) {
        return service.atualizar(email, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal String email, @PathVariable Long id) {
        service.excluir(email, id);
    }
}
