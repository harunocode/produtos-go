import { MAPA_CONFIG } from "../config/mapaConfig.js";
import { obterLocalizacaoAtual } from "../services/geolocalizacaoService.js";
import { obterLocalizacaoPorCep } from "../services/cepService.js";
import { buscarLojasProximas } from "../services/lojaService.js";
import { produtoService } from "../services/produtoService.js";

const elementoMapa = document.getElementById("mapaLeaflet");
const botaoLocalizacao = document.getElementById("btnLocalizacao");
const formularioCep = document.getElementById("formBuscaCep");
const campoCep = document.getElementById("cepLocalizacao");
const botaoBuscarCep = document.getElementById("btnBuscarCep");
const status = document.getElementById("statusLocalizacao");
const seletorRaio = document.getElementById("r");

let mapa;
let camadaLojas;
let marcadorUsuario;
let circuloRaio;
let posicaoUsuario;
let cepDaBusca;
let numeroBusca = 0;

function informar(mensagem) {
  if (status) status.textContent = mensagem;
}

function iniciarMapa() {
  if (!elementoMapa || !window.L) return;
  mapa = L.map(elementoMapa).setView(MAPA_CONFIG.centroInicial, MAPA_CONFIG.zoomInicial);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
  }).addTo(mapa);
  camadaLojas = L.layerGroup().addTo(mapa);
}

function escaparHtml(valor) {
  return String(valor).replace(/[&<>"']/g, c => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
  })[c]);
}

function categoriaParaApi() {
  const categoriaAtual = window.produtosGoCategoriaAtual || "todos";
  if (categoriaAtual === "todos") return null;
  const categoria = window.CATS?.find(([codigo]) => codigo === categoriaAtual);
  return categoria ? categoria[1] : categoriaAtual;
}

function desenharUsuario(raioKm) {
  const ponto = [posicaoUsuario.latitude, posicaoUsuario.longitude];
  if (marcadorUsuario) mapa.removeLayer(marcadorUsuario);
  if (circuloRaio) mapa.removeLayer(circuloRaio);
  marcadorUsuario = L.marker(ponto).addTo(mapa).bindPopup("<b>Você está aqui</b>");
  circuloRaio = L.circle(ponto, {
    radius: raioKm * 1000, color: "#2477d4", fillColor: "#2477d4", fillOpacity: 0.08
  }).addTo(mapa);
  mapa.setView(ponto, MAPA_CONFIG.zoomBusca);
}

function desenharLojas(lojas) {
  if (!camadaLojas) return;
  camadaLojas.clearLayers();
  lojas.forEach(loja => {
    if (typeof loja.latitude !== "number" || typeof loja.longitude !== "number") return;
    const km = Number(loja.distanciaMetros || 0) / 1000;
    const semHorario = !loja.horarios || loja.horarios.length === 0;
    const selo = window.Horarios && loja.statusHorario
      ? `<br>${window.Horarios.badge({ aberto: loja.aberto, neutro: semHorario, texto: loja.statusHorario })}`
      : "";
    const hoje = loja.horarioHoje ? `<br><small>Hoje: ${escaparHtml(loja.horarioHoje)}</small>` : "";
    L.marker([loja.latitude, loja.longitude])
      .bindPopup(`<b>${escaparHtml(loja.nome)}</b>${escaparHtml(loja.categoria || "Loja")}<br><small>${km.toFixed(2).replace(".", ",")} km</small>${selo}${hoje}`)
      .addTo(camadaLojas);
  });
}

async function atualizarBusca() {
  if (!posicaoUsuario) return;
  const buscaId = ++numeroBusca;
  const raioKm = Number(seletorRaio?.value || MAPA_CONFIG.raioPadraoKm);
  const categoria = categoriaParaApi();
  informar("Buscando lojas e produtos próximos...");
  window.iniciarCarregamentoProdutos?.();
  if (mapa) desenharUsuario(raioKm);
  const [resultadoLojas, resultadoProdutos] = await Promise.allSettled([
    buscarLojasProximas({ ...posicaoUsuario, raioKm, categoria }),
    produtoService.buscarProximos({
      ...posicaoUsuario,
      raioMetros: Math.round(raioKm * 1000),
      categoria
    })
  ]);
  if (buscaId !== numeroBusca) return;

  const mensagens = [];
  if (resultadoLojas.status === "fulfilled") {
    desenharLojas(resultadoLojas.value);
    mensagens.push(`${resultadoLojas.value.length} loja(s)`);
  } else {
    mensagens.push("Não foi possível carregar as lojas");
  }

  if (resultadoProdutos.status === "fulfilled") {
    window.definirProdutosDoBanco?.(resultadoProdutos.value);
    mensagens.push(`${resultadoProdutos.value.length} produto(s)`);
  } else {
    window.erroProdutosDoBanco?.(resultadoProdutos.reason?.message || "Falha ao consultar produtos.");
    mensagens.push("não foi possível carregar os produtos");
  }

  const origem = cepDaBusca
    ? ` a partir da localização aproximada do CEP ${cepDaBusca}`
    : "";
  informar(`${mensagens.join("; ")} em um raio de ${raioKm} km${origem}.`);
}

async function solicitarLocalizacao() {
  try {
    // O mapa pode ter sido inicializado enquanto a seção estava oculta.
    requestAnimationFrame(() => mapa?.invalidateSize());
    informar("Solicitando permissão de localização...");
    posicaoUsuario = await obterLocalizacaoAtual();
    cepDaBusca = null;
    await atualizarBusca();
  } catch (erro) {
    informar(erro.message);
  }
}

async function buscarPorCep(evento) {
  evento.preventDefault();
  if (!campoCep || !botaoBuscarCep) return;

  botaoBuscarCep.disabled = true;
  informar("Consultando o CEP...");
  try {
    const localizacao = await obterLocalizacaoPorCep(campoCep.value);
    posicaoUsuario = localizacao;
    cepDaBusca = campoCep.value.replace(/\D/g, "").replace(/^(\d{5})(\d{3})$/, "$1-$2");
    requestAnimationFrame(() => mapa?.invalidateSize());
    await atualizarBusca();
  } catch (erro) {
    informar(erro.message);
  } finally {
    botaoBuscarCep.disabled = false;
  }
}

function formatarCampoCep() {
  if (!campoCep) return;
  const digitos = campoCep.value.replace(/\D/g, "").slice(0, 8);
  campoCep.value = digitos.length > 5
    ? `${digitos.slice(0, 5)}-${digitos.slice(5)}`
    : digitos;
}

iniciarMapa();
botaoLocalizacao?.addEventListener("click", solicitarLocalizacao);
campoCep?.addEventListener("input", formatarCampoCep);
formularioCep?.addEventListener("submit", buscarPorCep);
seletorRaio?.addEventListener("change", atualizarBusca);
window.atualizarBuscaGeografica = atualizarBusca;
