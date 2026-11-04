import { apiRequest } from "../config/api.js";

// Endpoints previstos. Exigem implementação de autenticação no Spring Security.
export const authService = {
  entrar: (credenciais) => apiRequest("/auth/login", {
    method: "POST",
    body: JSON.stringify(credenciais),
  }),
  cadastrar: (dados) => apiRequest("/auth/registro", {
    method: "POST",
    body: JSON.stringify(dados),
  }),
};
