--Přidání sloupce id_zavod tabulce vratnice.lokalita

ALTER TABLE vratnice.lokalita
    ADD COLUMN id_zavod VARCHAR(14);

ALTER TABLE vratnice.lokalita
    ADD CONSTRAINT fk_lokalita_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION;


CREATE INDEX ix_lokalita_id_zavod ON vratnice.lokalita (id_zavod);

UPDATE vratnice.lokalita
SET id_zavod = 'XXZA0000000001'
WHERE id_lokalita = 'XXLK0000000001';

UPDATE vratnice.lokalita
SET id_zavod = 'XXZA0000000006'
WHERE id_lokalita = 'XXLK0000000002';

UPDATE vratnice.lokalita
SET id_zavod = 'XXZA0000000008'
WHERE id_lokalita = 'XXLK0000000003';