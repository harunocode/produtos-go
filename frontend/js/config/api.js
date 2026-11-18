// Configuração central da API REST.
// Em desenvolvimento, ajuste a URL conforme a porta do Spring Boot.
export const API_BASE_URL = "http://localhost:8080/api";

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem("produtosGoToken");
  const temCorpo = options.body != null;
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      Accept: "application/json",
      ...(temCorpo ? { "Content-Type": "application/json" } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    const message = await response.text().catch(() => "");
    let detalhe = message;
    try {
      const corpo = JSON.parse(message);
      detalhe = corpo.erro || corpo.message || message;
    } catch (_) {}
    throw new Error(detalhe || `Erro HTTP ${response.status}`);
  }

  if (response.status === 204) return null;
  return response.json();
}

window.produtosGoApiRequest = apiRequest;
