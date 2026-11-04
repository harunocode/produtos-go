# Produtos GO — Leaflet + Geolocalização + Spring Boot + PostGIS

## Arquitetura

```text
Navegador: Geolocation API → latitude/longitude
          Leaflet → mapa, posição do usuário e marcadores
                    │ HTTP/JSON
                    ▼
Spring Boot: Controller → Service → Repository
                    │ SQL espacial
                    ▼
PostgreSQL + PostGIS: GEOGRAPHY(POINT, 4326)
                      ST_DWithin (filtra pelo raio)
                      ST_Distance (calcula distância)
```

## Pastas e responsabilidades

### Front-end
- `frontend/index.html`: estrutura das telas.
- `frontend/css/`: estilos gerais e `mapa.css`.
- `frontend/js/demo.js`: demonstração original, ainda baseada em dados simulados.
- `frontend/js/config/mapaConfig.js`: URL e parâmetros do mapa.
- `frontend/js/services/geolocalizacaoService.js`: solicita localização ao navegador.
- `frontend/js/services/lojaService.js`: chama a API REST de lojas.
- `frontend/js/pages/mapaPage.js`: coordena Leaflet, localização e marcadores.

### Back-end
- `controller/LojaController.java`: endpoint HTTP.
- `service/LojaService.java`: valida os parâmetros.
- `repository/LojaRepository.java`: consulta SQL com PostGIS.
- `dto/LojaProximaResponse.java`: formato da resposta.
- `database/01_postgis_lojas.sql`: extensão, tabela e índice espacial.

## Endpoint
`GET /api/lojas/proximas`

Parâmetros: `latitude`, `longitude`, `raioMetros` (padrão 3000) e `categoria` (opcional).

Exemplo:
`http://localhost:8080/api/lojas/proximas?latitude=-29.92&longitude=-51.18&raioMetros=3000`

A resposta contém `id`, `nome`, `categoria`, `latitude`, `longitude` e `distanciaMetros`.

## Fluxo da busca
1. O usuário clica em “Usar minha localização”.
2. O navegador pede permissão.
3. O front-end envia latitude, longitude e raio ao Spring Boot.
4. O Service valida os parâmetros.
5. O Repository usa `ST_DWithin` e `ST_Distance`.
6. O Leaflet desenha os marcadores retornados.

## Preparação e execução
1. Crie o banco PostgreSQL `produtos_go`.
2. Execute `backend/produtos-go-api/database/01_postgis_lojas.sql`.
3. Cadastre lojas com coordenadas reais. Em `ST_MakePoint`, informe longitude antes da latitude.
4. Configure `DB_USER` e `DB_PASSWORD`, se necessário.
5. Execute o back-end com `mvn spring-boot:run` na pasta `backend/produtos-go-api`.
6. Abra o front-end por servidor local (por exemplo, Live Server).

A Geolocation API exige contexto seguro: `localhost` ou HTTPS. Não conte com geolocalização ao abrir o arquivo por `file://`.

## Limitações atuais
- O mapa Leaflet já consulta o endpoint espacial, mas o catálogo e as telas originais ainda mantêm a lista simulada `SHOPS`. A migração completa das telas para os serviços REST é uma etapa seguinte.
- A localização do visitante é usada na consulta e não é gravada no banco por este código.
- Não foram inventadas lojas de exemplo. A API retorna lista vazia até existirem lojas cadastradas.
- O CSS original não estava no arquivo recebido; `frontend/css/style.css` permanece como placeholder.
- Os tiles usados são do OpenStreetMap. Em produção, observe a política de uso dos tiles ou utilize um provedor adequado.
- O CRUD de produtos existente foi mantido.

## Modelo para documentar cada funcionalidade
1. Objetivo e ator.
2. Dados de entrada e validações.
3. Endpoint/método HTTP.
4. Fluxo Controller → Service → Repository → Banco.
5. Resposta JSON e códigos HTTP.
6. Erros possíveis.
7. Tela e service JavaScript relacionados.
8. Cenários de teste.

## Próximas etapas
1. Criar cadastro de lojas com endereço e coordenadas reais.
2. Migrar os produtos simulados para a API.
3. Integrar os filtros de categoria com a consulta geográfica.
4. Implementar autenticação e permissões.
5. Criar testes para raio, categoria e ordenação por distância.
