import { apiRequest } from "../config/api.js";

export const produtoService = {
  listar: () => apiRequest("/produtos"),
  buscarProximos: ({ latitude, longitude, raioMetros = 3000, categoria = null }) => {
    const params = new URLSearchParams({
      latitude: String(latitude),
      longitude: String(longitude),
      raioMetros: String(raioMetros),
    });
    if (categoria) params.set("categoria", categoria);
    return apiRequest(`/produtos/proximos?${params.toString()}`);
  },
  buscarPorId: (id) => apiRequest(`/produtos/${id}`),
  criar: (produto) => apiRequest("/produtos", {
    method: "POST",
    body: JSON.stringify(produto),
  }),
  atualizar: (id, produto) => apiRequest(`/produtos/${id}`, {
    method: "PUT",
    body: JSON.stringify(produto),
  }),
  excluir: (id) => apiRequest(`/produtos/${id}`, { method: "DELETE" }),
};
