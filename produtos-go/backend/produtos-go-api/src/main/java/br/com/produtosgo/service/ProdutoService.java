package br.com.produtosgo.service;

import br.com.produtosgo.dto.ProdutoRequest;
import br.com.produtosgo.dto.ProdutoResponse;
import br.com.produtosgo.dto.LojaProximaResponse;
import br.com.produtosgo.dto.ProdutoProximoResponse;
import br.com.produtosgo.entity.Categoria;
import br.com.produtosgo.entity.Loja;
import br.com.produtosgo.entity.Produto;
import br.com.produtosgo.exception.RecursoNaoEncontradoException;
import br.com.produtosgo.repository.CategoriaRepository;
import br.com.produtosgo.repository.LojaJpaRepository;
import br.com.produtosgo.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;
    private final LojaJpaRepository lojaRepository;
    private final CategoriaRepository categoriaRepository;
    private final LojaService lojaService;

    public ProdutoService(ProdutoRepository repository,
                          LojaJpaRepository lojaRepository,
                          CategoriaRepository categoriaRepository,
                          LojaService lojaService) {
        this.repository = repository;
        this.lojaRepository = lojaRepository;
        this.categoriaRepository = categoriaRepository;
        this.lojaService = lojaService;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return repository.findAll().stream().map(ProdutoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoProximoResponse> buscarProximos(
        double latitude, double longitude, int raioMetros, String categoria
    ) {
        List<LojaProximaResponse> lojas = lojaService.buscarProximas(
            latitude, longitude, raioMetros,
            StringUtils.hasText(categoria) ? categoria : null
        );
        if (lojas.isEmpty()) return List.of();

        Map<Long, LojaProximaResponse> lojaPorId = lojas.stream()
            .collect(Collectors.toMap(LojaProximaResponse::id, Function.identity()));
        List<Produto> produtos = repository.buscarDisponiveisNasLojas(lojaPorId.keySet());

        return produtos.stream().map(produto -> new ProdutoProximoResponse(
            produto.getId(), produto.getNome(), produto.getPreco(), produto.getEstoque(),
            produto.getDescricao(), produto.getCategoria().getId(),
            lojaPorId.get(produto.getLoja().getId())
        )).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return ProdutoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto produto = new Produto();
        aplicarDados(produto, request);
        return ProdutoResponse.from(repository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        aplicarDados(produto, request);
        return ProdutoResponse.from(repository.save(produto));
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        repository.delete(produto);
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + id + " não encontrado."));
    }

    private void aplicarDados(Produto produto, ProdutoRequest request) {
        Loja loja = lojaRepository.findById(request.lojaId())
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Loja " + request.lojaId() + " não encontrada."));
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Categoria " + request.categoriaId() + " não encontrada."));

        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());
        produto.setDescricao(request.descricao());
        produto.setAtivo(request.ativo() == null || request.ativo());
        produto.setLoja(loja);
        produto.setCategoria(categoria);
    }
}
