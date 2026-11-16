package br.com.produtosgo.dto;

public record LojaProximaResponse(
        Long id,
        String nome,
        Integer categoriaId,
        Double latitude,
        Double longitude,
        Double distanciaMetros
) {}