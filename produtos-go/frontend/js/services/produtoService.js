import { apiRequest } from "../config/api.js";

export const produtoService = {
  listar: () => apiRequest("/produtos"),
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
