/*
 * Produtos GO — busca com tolerância a erros de digitação.
 *
 * Regras:
 *  - Ignora maiúsculas, acentos e pontuação ("pao" encontra "Pão francês").
 *  - Cada palavra digitada precisa casar com alguma palavra do nome:
 *      igual > começa com (digitação em andamento) > contém (4+ letras) > parecida.
 *  - Tolerância a erros só para palavras com 5 letras ou mais:
 *      5 a 7 letras = 1 erro; 8 ou mais = 2 erros.
 *    Um erro é uma letra trocada, faltando, sobrando ou duas letras invertidas
 *    ("cahve" -> "chave"). Palavras curtas exigem acerto exato do início.
 */
(function (global) {
  "use strict";

  var MIN_LETRAS_TOLERANCIA = 5;

  function normalizar(texto) {
    return String(texto)
      .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .replace(/[^a-z0-9\s]/g, " ")
      .replace(/\s+/g, " ")
      .trim();
  }

  function palavras(texto) {
    var n = normalizar(texto);
    return n ? n.split(" ") : [];
  }

  /** Distância de edição que conta a troca de duas letras vizinhas como 1 erro. */
  function distancia(a, b) {
    var la = a.length, lb = b.length, i, j;
    if (!la) return lb;
    if (!lb) return la;
    var d = [];
    for (i = 0; i <= la; i++) { d[i] = [i]; }
    for (j = 1; j <= lb; j++) { d[0][j] = j; }
    for (i = 1; i <= la; i++) {
      for (j = 1; j <= lb; j++) {
        var custo = a.charAt(i - 1) === b.charAt(j - 1) ? 0 : 1;
        var v = Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + custo);
        if (i > 1 && j > 1 && a.charAt(i - 1) === b.charAt(j - 2) && a.charAt(i - 2) === b.charAt(j - 1)) {
          v = Math.min(v, d[i - 2][j - 2] + 1);
        }
        d[i][j] = v;
      }
    }
    return d[la][lb];
  }

  function tolerancia(tamanho) {
    if (tamanho < MIN_LETRAS_TOLERANCIA) return 0;
    return tamanho <= 7 ? 1 : 2;
  }

  /** Pontuação (menor = melhor) de uma palavra digitada contra uma palavra do nome; -1 se não casa. */
  function pontuarPalavra(q, p) {
    if (p === q) return 0;
    if (p.indexOf(q) === 0) return 1;
    if (q.length >= 4 && p.indexOf(q) > 0) return 2;
    var tol = tolerancia(q.length);
    if (tol > 0) {
      // compara com a palavra inteira e com o começo dela (para quem ainda está digitando)
      var d = Math.min(distancia(q, p), distancia(q, p.slice(0, q.length)));
      if (d <= tol) return 3 + d;
    }
    return -1;
  }

  /** Pontuação da consulta contra um texto; -1 se alguma palavra digitada não casa. */
  function pontuar(consulta, texto) {
    var qs = palavras(consulta), ps = palavras(texto), total = 0;
    if (!qs.length) return 0;
    for (var i = 0; i < qs.length; i++) {
      var melhor = -1;
      for (var j = 0; j < ps.length; j++) {
        var s = pontuarPalavra(qs[i], ps[j]);
        if (s >= 0 && (melhor < 0 || s < melhor)) melhor = s;
      }
      if (melhor < 0) return -1;
      total += melhor;
    }
    return total;
  }

  function combina(consulta, texto) {
    return pontuar(consulta, texto) >= 0;
  }

  /** Filtra e ordena itens ({ nome, ... }) pelo melhor casamento com a consulta. */
  function sugerir(consulta, itens, limite) {
    return itens
      .map(function (it) { return { item: it, pontos: pontuar(consulta, it.nome) }; })
      .filter(function (x) { return x.pontos >= 0; })
      .sort(function (a, b) {
        return a.pontos - b.pontos ||
          a.item.nome.length - b.item.nome.length ||
          a.item.nome.localeCompare(b.item.nome, "pt-BR");
      })
      .slice(0, limite || 6)
      .map(function (x) { return x.item; });
  }

  global.Busca = {
    normalizar: normalizar, distancia: distancia, pontuar: pontuar, combina: combina, sugerir: sugerir
  };
})(window);
