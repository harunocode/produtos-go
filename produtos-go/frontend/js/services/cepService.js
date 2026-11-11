const API_CEP_URL = "https://brasilapi.com.br/api/cep/v2/";
const localizacoesPorCep = new Map();

/** Consulta as coordenadas associadas a um CEP brasileiro. */
export async function obterLocalizacaoPorCep(valorCep) {
  const cep = String(valorCep ?? "").replace(/\D/g, "");
  if (!/^\d{8}$/.test(cep)) {
    throw new Error("Digite um CEP válido com 8 números.");
  }

  if (localizacoesPorCep.has(cep)) return localizacoesPorCep.get(cep);

  let resposta;
  try {
    resposta = await fetch(`${API_CEP_URL}${cep}`, {
      headers: { Accept: "application/json" }
    });
  } catch {
    throw new Error("Não foi possível consultar o CEP. Verifique sua conexão e tente novamente.");
  }

  if (resposta.status === 404) {
    throw new Error("CEP não encontrado. Confira os números e tente novamente.");
  }
  if (!resposta.ok) {
    throw new Error("O serviço de CEP está indisponível agora. Tente novamente em instantes.");
  }

  let dados;
  try {
    dados = await resposta.json();
  } catch {
    throw new Error("O serviço de CEP retornou uma resposta inválida. Tente novamente.");
  }

  const coordenadas = dados.location?.coordinates;
  if (coordenadas?.latitude == null || coordenadas?.longitude == null
    || coordenadas.latitude === "" || coordenadas.longitude === "") {
    throw new Error("Não há coordenadas disponíveis para esse CEP. Tente outro CEP ou use sua localização.");
  }
  const latitude = Number(coordenadas.latitude);
  const longitude = Number(coordenadas.longitude);
  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)
    || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
    throw new Error("Não há coordenadas disponíveis para esse CEP. Tente outro CEP ou use sua localização.");
  }

  const localizacao = { latitude, longitude };
  localizacoesPorCep.set(cep, localizacao);
  return localizacao;
}
