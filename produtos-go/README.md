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
- `frontend/js/demo.js`: renderiza a busca e os detalhes dos produtos recebidos da API; autenticação e painel da loja continuam demonstrativos.
- `frontend/js/busca.js`: busca tolerante a erros de digitação (usada pelas sugestões e pelo filtro de produtos).
- `frontend/js/horarios.js`: calcula se a loja está aberta ou fechada, monta o texto ("Aberto · fecha às 18h") e a lista da semana.
- `frontend/js/config/mapaConfig.js`: URL e parâmetros do mapa.
- `frontend/js/services/geolocalizacaoService.js`: solicita localização ao navegador.
- `frontend/js/services/lojaService.js`: chama a API REST de lojas.
- `frontend/js/pages/mapaPage.js`: coordena Leaflet, localização e marcadores.

### Back-end
- `controller/LojaController.java`: endpoint HTTP.
- `service/LojaService.java`: valida os parâmetros.
- `repository/LojaRepository.java`: consulta SQL com PostGIS.
- `dto/LojaProximaResponse.java`: formato da resposta.
- `dto/HorarioDia.java`: um intervalo de funcionamento.
- `service/HorarioFuncionamento.java`: regra de "aberto agora" (fuso de Brasília).
- `database/01_postgis_lojas.sql`: extensão, tabela e índice espacial.
- `database/02_horarios_lojas.sql`: tabela `lojas_horarios` e horários fictícios por categoria.

## Endpoint
`GET /api/lojas/proximas`

Parâmetros: `latitude`, `longitude`, `raioMetros` (padrão 3000) e `categoria` (opcional).

Exemplo:
`http://localhost:8080/api/lojas/proximas?latitude=-29.92&longitude=-51.18&raioMetros=3000`

A resposta contém `id`, `nome`, `categoria`, `latitude`, `longitude`, `distanciaMetros` e, para o horário de funcionamento:

- `aberto`: `true` se a loja está aberta agora (fuso de Brasília);
- `statusHorario`: texto pronto, ex.: `Aberto · fecha às 18h` ou `Fechado · abre amanhã às 8h`;
- `horarioHoje`: ex.: `8h–12h e 13h30–18h` (`Fechado` se não abre hoje; `null` se a loja não tem horários cadastrados);
- `horarios`: lista de intervalos (`diaSemana` de 1 = segunda a 7 = domingo, `abre`, `fecha` em `HH:mm`).

`GET /api/produtos/proximos?latitude=...&longitude=...&raioMetros=3000&categoria=Ferragem`

Retorna os produtos ativos e com estoque das lojas ativas dentro do raio, junto com os dados e horários da loja. A tela chama os dois endpoints quando o usuário compartilha a localização; a busca por texto, faixa de preço e sugestões filtra os produtos recebidos.

## Fluxo da busca
1. O usuário clica em “Usar minha localização”.
2. O navegador pede permissão.
3. O front-end envia latitude, longitude e raio ao Spring Boot.
4. O Service valida os parâmetros.
5. O Repository usa `ST_DWithin` e `ST_Distance`.
6. O Leaflet desenha os marcadores retornados.

## Preparação e execução
1. Crie o banco PostgreSQL `produtos_go`.
2. No Supabase, aplique o esquema final de categorias, lojas, horários, produtos e avaliações. Os arquivos `database/01_postgis_lojas.sql` e `02_horarios_lojas.sql` ainda são o esquema legado e não criam as tabelas novas de produtos/categorias.
3. Cadastre categorias, lojas, horários e produtos. Em `ST_MakePoint`, informe longitude antes da latitude.
4. Configure `DB_USER` e `DB_PASSWORD`, se necessário.
5. Execute o back-end com `mvn spring-boot:run` na pasta `backend/produtos-go-api`.
6. Abra o front-end por servidor local (por exemplo, Live Server).

A Geolocation API exige contexto seguro: `localhost` ou HTTPS. Não conte com geolocalização ao abrir o arquivo por `file://`.

## Limitações atuais
- O catálogo consulta a API de produtos próximos e o mapa consulta as lojas próximas. Favoritos e curtidas ficam apenas na sessão do navegador.
- A localização do visitante é usada na consulta e não é gravada no banco por este código.
- Não foram inventadas lojas de exemplo. A API retorna lista vazia até existirem lojas cadastradas.
- O CSS original não estava no arquivo recebido; `frontend/css/style.css` permanece como placeholder.
- Os tiles usados são do OpenStreetMap. Em produção, observe a política de uso dos tiles ou utilize um provedor adequado.
- O CRUD REST de produtos existe; o painel da loja ainda não usa esse CRUD e o login continua sendo demonstração.
- A busca usa os horários cadastrados no banco. Ainda não há tela para a loja editar seus horários.
- Horários que viram a madrugada (ex.: 18h–2h) e feriados não são tratados.
- O selo aberto/fechado dos cards é recalculado a cada minuto; no mapa, o status vem da API no momento da busca.

## Busca com sugestões (front-end)
Ao digitar no campo de busca, uma lista de produtos aparece abaixo com os nomes que combinam com o texto. Os resultados da tela também são filtrados com a mesma regra.

- Ignora maiúsculas, acentos e pontuação (`pao` encontra `Pão francês`).
- Aceita o começo da palavra enquanto o usuário digita (`chav` → Chave de fenda).
- Tolera erros de digitação em palavras com **5 letras ou mais**: 1 erro em palavras de 5 a 7 letras e até 2 em palavras de 8 ou mais. Conta letra errada, faltando, sobrando ou duas letras trocadas de lugar (`cahve` → Chave de fenda). Palavras menores exigem o início correto (`cahv` não encontra nada).
- As sugestões respeitam os filtros ativos (categoria, raio e faixa de preço) e mostram quantas lojas têm o produto e o menor preço.
- Teclado: ↓ e ↑ navegam, Enter escolhe, Esc fecha. Clicar numa sugestão preenche o campo e filtra os resultados.
- A busca tolerante e as sugestões rodam no navegador sobre os produtos próximos que a API carregou; uma busca global pode ser movida para o PostgreSQL com `pg_trgm`.

## Validações do cadastro (front-end)
Aplicadas em `demo.js` ao criar conta, nesta ordem:

1. E-mail precisa conter `@` ("O campo e-mail deve conter obrigatoriamente o caractere @.") e ter formato válido.
2. Senha com no mínimo 6 caracteres, ao menos 1 número e 1 caractere especial ("A senha não contém os requisitos mínimos.").
3. Confirmação de senha idêntica à senha ("A senha digitada não corresponde à anterior.").

O login continua exigindo apenas e-mail válido e senha de 6+ caracteres. Estas regras rodam só no navegador (demonstração): quando o cadastro for para a API, repita-as no back-end.

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
2. Integrar o painel de cadastro da loja ao CRUD REST de produtos.
3. Implementar autenticação e validação de propriedade da loja.
4. Criar telas para cadastrar categorias, lojas e horários.
5. Criar testes para raio, categoria e ordenação por distância.
