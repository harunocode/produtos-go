package br.com.produtosgo.dto;

public record LojaProximaResponse(
    Long id, String nome, String categoria,
    Double latitude, Double longitude, Double distanciaMetros
) {}
