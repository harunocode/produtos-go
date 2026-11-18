CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('CLIENTE', 'ESTABELECIMENTO', 'ADMIN')),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorias (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE
);
INSERT INTO categorias (id, nome) VALUES
    (1, 'Padaria'), (2, 'Mercado'), (3, 'Ferragem'), (4, 'Bazar'),
    (5, 'Papelaria'), (6, 'Hortifruti'), (7, 'Pet Shop'),
    (8, U&'Farmac\00EDa'), (9, U&'Sal\00E3o de Beleza'),
    (10, U&'Oficina Mec\00E2nica'), (11, 'Loja de Roupas');

CREATE TABLE lojas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE REFERENCES usuarios(id) ON DELETE SET NULL,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    categoria_id INTEGER NOT NULL REFERENCES categorias(id),
    cep VARCHAR(8),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    localizacao GEOGRAPHY(POINT, 4326) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_lojas_localizacao ON lojas USING GIST (localizacao);
CREATE INDEX idx_lojas_categoria_status ON lojas (categoria_id, status);

CREATE TABLE lojas_horarios (
    id BIGSERIAL PRIMARY KEY,
    loja_id BIGINT NOT NULL REFERENCES lojas(id) ON DELETE CASCADE,
    dia_semana SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    horario_abertura TIME NOT NULL,
    horario_fechamento TIME NOT NULL,
    CHECK (horario_fechamento > horario_abertura),
    UNIQUE (loja_id, dia_semana, horario_abertura)
);
CREATE INDEX idx_lojas_horarios_loja ON lojas_horarios (loja_id);

CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    preco NUMERIC(12, 2) NOT NULL CHECK (preco >= 0),
    estoque INTEGER NOT NULL DEFAULT 0 CHECK (estoque >= 0),
    descricao VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    loja_id BIGINT NOT NULL REFERENCES lojas(id) ON DELETE CASCADE,
    categoria_id INTEGER NOT NULL REFERENCES categorias(id),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_produtos_loja_ativo_estoque ON produtos (loja_id, ativo, estoque);

INSERT INTO lojas (nome, categoria_id, cep, localizacao) VALUES
    ('Padaria Pao Quente', 1, '92010000', ST_SetSRID(ST_MakePoint(-51.1900,-29.9150),4326)::geography),
    ('Mercado Central', 2, '92020000', ST_SetSRID(ST_MakePoint(-51.1770,-29.9220),4326)::geography),
    ('Ferragens Canoas', 3, '92010120', ST_SetSRID(ST_MakePoint(-51.1836,-29.9178),4326)::geography);

INSERT INTO lojas_horarios (loja_id, dia_semana, horario_abertura, horario_fechamento)
SELECT l.id,d.dia,d.abre,d.fecha FROM lojas l CROSS JOIN (VALUES
 (1,TIME '06:00',TIME '20:00'),(2,TIME '06:00',TIME '20:00'),
 (3,TIME '06:00',TIME '20:00'),(4,TIME '06:00',TIME '20:00'),
 (5,TIME '06:00',TIME '20:00'),(6,TIME '06:00',TIME '20:00'),
 (7,TIME '06:00',TIME '12:00')) d(dia,abre,fecha)
WHERE l.nome = 'Padaria Pao Quente';
INSERT INTO lojas_horarios (loja_id, dia_semana, horario_abertura, horario_fechamento)
SELECT l.id,d.dia,d.abre,d.fecha FROM lojas l CROSS JOIN (VALUES
 (1,TIME '07:30',TIME '22:00'),(2,TIME '07:30',TIME '22:00'),
 (3,TIME '07:30',TIME '22:00'),(4,TIME '07:30',TIME '22:00'),
 (5,TIME '07:30',TIME '22:00'),(6,TIME '07:30',TIME '22:00'),
 (7,TIME '08:00',TIME '20:00')) d(dia,abre,fecha)
WHERE l.nome = 'Mercado Central';
INSERT INTO lojas_horarios (loja_id, dia_semana, horario_abertura, horario_fechamento)
SELECT l.id,d.dia,d.abre,d.fecha FROM lojas l CROSS JOIN (VALUES
 (1,TIME '08:00',TIME '18:00'),(2,TIME '08:00',TIME '18:00'),
 (3,TIME '08:00',TIME '18:00'),(4,TIME '08:00',TIME '18:00'),
 (5,TIME '08:00',TIME '18:00'),(6,TIME '08:00',TIME '12:00'))
 d(dia,abre,fecha) WHERE l.nome = 'Ferragens Canoas';

INSERT INTO produtos (nome, preco, estoque, descricao, loja_id, categoria_id)
SELECT p.nome,p.preco,p.estoque,p.descricao,l.id,l.categoria_id FROM (VALUES
 (U&'P\00E3o franc\00EAs',0.80::NUMERIC,80,'Unidade','Padaria Pao Quente'),
 ('Cuca caseira',18.90::NUMERIC,8,'Sabores do dia','Padaria Pao Quente'),
 ('Leite integral 1L',5.49::NUMERIC,30,'Caixa de leite','Mercado Central'),
 ('Arroz 5 kg',27.90::NUMERIC,15,'Pacote','Mercado Central'),
 ('Chave de fenda',14.90::NUMERIC,12,'Cabo emborrachado','Ferragens Canoas'),
 ('Fita isolante',6.50::NUMERIC,25,'Rolo de 10 metros','Ferragens Canoas')
) p(nome,preco,estoque,descricao,loja_nome) JOIN lojas l ON l.nome=p.loja_nome;
