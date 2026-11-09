/*
 * Produtos GO — horários de funcionamento das lojas.
 *
 * Formato de uma semana: array de 7 posições (0 = domingo ... 6 = sábado).
 * Cada posição é uma lista de intervalos [abre, fecha], ex.: [["08:00","12:00"],["13:30","18:00"]].
 * Lista vazia = loja fechada nesse dia.
 *
 * O cálculo usa sempre o fuso de Brasília, para que "aberto agora" não dependa
 * do fuso configurado no computador de quem acessa.
 * Limitação: não há suporte a horários que viram a madrugada (ex.: 18:00–02:00).
 */
(function (global) {
  "use strict";

  var FUSO = "America/Sao_Paulo";
  var DIAS = ["Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"];
  var DIAS_CURTOS = ["dom", "seg", "ter", "qua", "qui", "sex", "sáb"];
  var DIA_POR_SIGLA = { Sun: 0, Mon: 1, Tue: 2, Wed: 3, Thu: 4, Fri: 5, Sat: 6 };

  function esc(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;" }[c];
    });
  }

  /** Dia da semana e minutos desde 00:00, no fuso de Brasília. */
  function agora(data) {
    var partes = new Intl.DateTimeFormat("en-US", {
      timeZone: FUSO, weekday: "short", hour: "2-digit", minute: "2-digit", hourCycle: "h23"
    }).formatToParts(data || new Date());
    var o = {};
    partes.forEach(function (p) { o[p.type] = p.value; });
    return {
      dia: DIA_POR_SIGLA[o.weekday],
      minutos: (parseInt(o.hour, 10) % 24) * 60 + parseInt(o.minute, 10)
    };
  }

  function emMinutos(hhmm) {
    var p = hhmm.split(":");
    return parseInt(p[0], 10) * 60 + parseInt(p[1], 10);
  }

  /** "08:00" -> "8h"; "13:30" -> "13h30". */
  function fmt(hhmm) {
    var p = hhmm.split(":"), h = parseInt(p[0], 10), m = parseInt(p[1], 10);
    return h + "h" + (m ? (m < 10 ? "0" + m : m) : "");
  }

  function intervalosTexto(lista) {
    if (!lista || !lista.length) return "Fechado";
    return lista.map(function (iv) { return fmt(iv[0]) + "–" + fmt(iv[1]); }).join(" e ");
  }

  /**
   * Monta a semana a partir de três grupos.
   * Ex.: montar({ semana: [["08:00","18:00"]], sabado: [["08:00","12:00"]], domingo: [] })
   */
  function montar(def) {
    var semana = def.semana || [], sabado = def.sabado || [], domingo = def.domingo || [];
    return [domingo, semana, semana, semana, semana, semana, sabado];
  }

  /**
   * Situação da loja no momento informado (padrão: agora).
   * Retorna { aberto, neutro, texto, hoje }.
   */
  function status(semana, data) {
    var temHorario = semana && semana.some(function (d) { return d && d.length; });
    if (!temHorario) return { aberto: false, neutro: true, texto: "Horário não informado", hoje: null };

    var t = agora(data);
    var hoje = semana[t.dia] || [];
    var resultado = { aberto: false, neutro: false, texto: "", hoje: intervalosTexto(hoje) };

    for (var i = 0; i < hoje.length; i++) {
      if (t.minutos >= emMinutos(hoje[i][0]) && t.minutos < emMinutos(hoje[i][1])) {
        resultado.aberto = true;
        resultado.texto = "Aberto · fecha às " + fmt(hoje[i][1]);
        return resultado;
      }
    }
    for (var j = 0; j < hoje.length; j++) {
      if (emMinutos(hoje[j][0]) > t.minutos) {
        resultado.texto = "Fechado · abre às " + fmt(hoje[j][0]);
        return resultado;
      }
    }
    for (var k = 1; k <= 7; k++) {
      var dia = (t.dia + k) % 7, lista = semana[dia] || [];
      if (lista.length) {
        resultado.texto = "Fechado · abre " + (k === 1 ? "amanhã" : DIAS_CURTOS[dia]) + " às " + fmt(lista[0][0]);
        return resultado;
      }
    }
    resultado.texto = "Fechado";
    return resultado;
  }

  /** Selo "● Aberto · fecha às 18h". Não depende só da cor: o texto diz aberto/fechado. */
  function badge(st) {
    var cls = st.neutro ? "neutro" : st.aberto ? "aberto" : "fechado";
    return '<span class="horario ' + cls + '">' + esc(st.texto) + "</span>";
  }

  /** Lista da semana inteira, com o dia atual em destaque. */
  function semanaHtml(semana, data) {
    if (!semana || !semana.some(function (d) { return d && d.length; })) return "";
    var hojeIdx = agora(data).dia;
    var ordem = [1, 2, 3, 4, 5, 6, 0];
    return '<details class="horarios-semana"><summary>Ver horários da semana</summary><ul>' +
      ordem.map(function (d) {
        return "<li" + (d === hojeIdx ? ' class="hoje"' : "") + "><span>" + DIAS[d] + "</span><span>" +
          esc(intervalosTexto(semana[d])) + "</span></li>";
      }).join("") + "</ul></details>";
  }

  global.Horarios = {
    montar: montar, status: status, badge: badge, semanaHtml: semanaHtml, agora: agora
  };
})(window);
