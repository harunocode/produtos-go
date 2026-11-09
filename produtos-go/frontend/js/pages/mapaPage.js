import { MAPA_CONFIG } from "../config/mapaConfig.js";
import { obterLocalizacaoAtual } from "../services/geolocalizacaoService.js";
import { buscarLojasProximas } from "../services/lojaService.js";

const elementoMapa = document.getElementById("mapaLeaflet");
const botaoLocalizacao = document.getElementById("btnLocalizacao");
const status = document.getElementById("statusLocalizacao");
const seletorRaio = document.getElementById("r");

let mapa;
let camadaLojas;
let marcadorUsuario;
let circuloRaio;
let posicaoUsuario;

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
  if (!posicaoUsuario || !mapa) return;
  const raioKm = Number(seletorRaio?.value || MAPA_CONFIG.raioPadraoKm);
  informar("Buscando lojas próximas...");
  desenharUsuario(raioKm);
  try {
    const lojas = await buscarLojasProximas({
      ...posicaoUsuario,
      raioKm,
      categoria: window.produtosGoCategoriaAtual || "todos"
    });
    desenharLojas(lojas);
    informar(`${lojas.length} loja(s) encontrada(s) em um raio de ${raioKm} km.`);
  } catch (erro) {
    informar(`Não foi possível consultar a API: ${erro.message}`);
  }
}

async function solicitarLocalizacao() {
  try {
    // O mapa pode ter sido inicializado enquanto a seção estava oculta.
    requestAnimationFrame(() => mapa?.invalidateSize());
    informar("Solicitando permissão de localização...");
    posicaoUsuario = await obterLocalizacaoAtual();
    await atualizarBusca();
  } catch (erro) {
    informar(erro.message);
  }
}

iniciarMapa();
botaoLocalizacao?.addEventListener("click", solicitarLocalizacao);
seletorRaio?.addEventListener("change", atualizarBusca);
window.atualizarBuscaGeografica = atualizarBusca;
