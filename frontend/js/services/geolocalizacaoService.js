/**
 * Encapsula a Geolocation API do navegador.
 * A posição só é obtida após autorização explícita do usuário.
 */
export function obterLocalizacaoAtual() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Este navegador não oferece suporte à geolocalização."));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => resolve({
        latitude: coords.latitude,
        longitude: coords.longitude,
        precisaoMetros: coords.accuracy
      }),
      (erro) => {
        const mensagens = {
          1: "Permissão de localização negada. Autorize o acesso nas configurações do navegador.",
          2: "Não foi possível determinar sua localização.",
          3: "A solicitação de localização expirou. Tente novamente."
        };
        reject(new Error(mensagens[erro.code] || "Falha ao obter localização."));
      },
      { enableHighAccuracy: true, timeout: 15000, maximumAge: 60000 }
    );
  });
}
