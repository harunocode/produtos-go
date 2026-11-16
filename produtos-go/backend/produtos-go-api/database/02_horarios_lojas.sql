-- Horários de funcionamento das lojas. Execute depois de 01_postgis_lojas.sql.
-- dia_semana segue a ISO-8601: 1 = segunda ... 7 = domingo.
-- Uma loja pode ter mais de um intervalo no mesmo dia (ex.: pausa para o almoço).
-- Não há suporte a horários que viram a madrugada (fecha deve ser maior que abre).
CREATE TABLE IF NOT EXISTS lojas_horarios (
    id BIGSERIAL PRIMARY KEY,
    loja_id BIGINT NOT NULL REFERENCES lojas(id) ON DELETE CASCADE,
    dia_semana SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    abre TIME NOT NULL,
    fecha TIME NOT NULL,
    CHECK (fecha > abre),
    UNIQUE (loja_id, dia_semana, abre)
);

CREATE INDEX IF NOT EXISTS idx_lojas_horarios_loja ON lojas_horarios (loja_id);

-- HORÁRIOS FICTÍCIOS (apenas para demonstração).
-- Gera um horário padrão por categoria para toda loja que ainda não tenha horário:
--   padaria:           seg-sáb 06h-20h, dom 06h-12h
--   mercado/farmácia:  seg-sáb 07h30-22h, dom 08h-20h
--   demais categorias: seg-sex 08h-18h, sáb 08h-12h, dom fechado
-- Pode ser executado de novo após cadastrar novas lojas; não duplica nem altera as que já têm horário.
INSERT INTO lojas_horarios (loja_id, dia_semana, abre, fecha)
SELECT l.id, d.dia,
       (CASE WHEN l.categoria = 'padaria'               THEN '06:00'
             WHEN l.categoria IN ('mercado', 'farmacia') THEN '07:30'
             ELSE '08:00' END)::time,
       (CASE WHEN l.categoria = 'padaria'               THEN (CASE WHEN d.dia = 7 THEN '12:00' ELSE '20:00' END)
             WHEN l.categoria IN ('mercado', 'farmacia') THEN (CASE WHEN d.dia = 7 THEN '20:00' ELSE '22:00' END)
             ELSE (CASE WHEN d.dia = 6 THEN '12:00' ELSE '18:00' END) END)::time
  FROM lojas l
 CROSS JOIN generate_series(1, 7) AS d(dia)
 WHERE NOT EXISTS (SELECT 1 FROM lojas_horarios h WHERE h.loja_id = l.id)
   AND (d.dia <= 6 OR l.categoria IN ('padaria', 'mercado', 'farmacia'));

-- Exemplo de horário manual para uma loja (segunda a sexta, com pausa para o almoço):
-- INSERT INTO lojas_horarios (loja_id, dia_semana, abre, fecha)
-- SELECT 1, dia, t.abre::time, t.fecha::time
--   FROM generate_series(1, 5) AS dia
--  CROSS JOIN (VALUES ('08:00', '12:00'), ('13:30', '18:00')) AS t(abre, fecha);
