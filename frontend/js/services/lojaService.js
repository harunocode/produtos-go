import { MAPA_CONFIG } from "../config/mapaConfig.js";

/** Centraliza as requisições HTTP relacionadas às lojas. */
export async function buscarLojasProximas({ latitude, longitude, raioKm = 3, categoria = null }) {
  const url = new URL(MAPA_CONFIG.apiLojasProximas);
  url.searchParams.set("latitude", latitude);
  url.searchParams.set("longitude", longitude);
  url.searchParams.set("raioMetros", Math.round(raioKm * 1000));
  if (categoria && categoria !== "todos") url.searchParams.set("categoria", categoria);

  const resposta = await fetch(url);
  if (!resposta.ok) throw new Error(`Falha ao consultar lojas (HTTP ${resposta.status}).`);
  return resposta.json();
}
