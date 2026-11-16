// Configuração central da API REST.
// Em desenvolvimento, ajuste a URL conforme a porta do Spring Boot.
export const API_BASE_URL = "http://localhost:8080/api";

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Erro HTTP ${response.status}`);
  }

  if (response.status === 204) return null;
  return response.json();
}
