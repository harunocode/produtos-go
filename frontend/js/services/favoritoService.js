import { apiRequest } from "../config/api.js";

// Endpoints previstos: dependem do módulo de usuários/favoritos.
export const favoritoService = {
  listar: () => apiRequest("/favoritos"),
  adicionar: (produtoId) => apiRequest("/favoritos", {
    method: "POST",
    body: JSON.stringify({ produtoId }),
  }),
  remover: (produtoId) => apiRequest(`/favoritos/${produtoId}`, {
    method: "DELETE",
  }),
};
