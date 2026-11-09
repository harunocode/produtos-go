-- Execute no banco produtos_go antes de usar /api/lojas/proximas.
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS lojas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    categoria VARCHAR(80),
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    localizacao GEOGRAPHY(POINT, 4326) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_lojas_localizacao
    ON lojas USING GIST (localizacao);

-- Exemplo: substitua pelas coordenadas reais da loja.
-- ST_MakePoint recebe LONGITUDE primeiro e LATITUDE depois.
-- INSERT INTO lojas (nome, categoria, ativa, localizacao)
-- VALUES ('Loja Exemplo', 'ferragem', TRUE,
--   ST_SetSRID(ST_MakePoint(-51.18, -29.92), 4326)::geography);
