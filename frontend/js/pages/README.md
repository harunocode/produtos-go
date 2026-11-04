# Organização das páginas

- `mapaPage.js`: coordena a tela do mapa; não conhece SQL nem persistência.
- `services/geolocalizacaoService.js`: encapsula a API do navegador.
- `services/lojaService.js`: encapsula as chamadas HTTP.
- `config/mapaConfig.js`: configura URL, centro e zoom.
- `demo.js`: código legado da demonstração. Migrar cada funcionalidade para os services gradualmente.
