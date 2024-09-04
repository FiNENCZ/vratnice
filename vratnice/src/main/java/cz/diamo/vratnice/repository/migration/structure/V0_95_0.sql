-- Nejprve zrušíme indexy, které jsou spojeny s tabulkou
DROP INDEX IF EXISTS vratnice.ix_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla;
DROP INDEX IF EXISTS vratnice.ix_povoleni_vjezdu_vozidla_zavod_id_zavod;

-- Následně odstraníme tabulku, čímž se automaticky zruší i primární klíč a cizí klíče
DROP TABLE IF EXISTS vratnice.povoleni_vjezdu_vozidla_zavod CASCADE;


-- Vytvoření tabulky ManyToMany pro povoleni - lokalita
CREATE TABLE IF NOT EXISTS vratnice.povoleni_vjezdu_vozidla_lokalita (
    id_povoleni_vjezdu_vozidla VARCHAR(14) NOT NULL,
    id_lokalita VARCHAR(14) NOT NULL,
    CONSTRAINT pk_povoleni_vjezdu_vozidla_lokalita  PRIMARY KEY (id_povoleni_vjezdu_vozidla, id_lokalita),
    CONSTRAINT fk_povoleni_vjezdu_vozidla_lokalita_id_povoleni_vjezdu_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_povoleni_vjezdu_vozidla_lokalita_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita (id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_lokalita_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_lokalita (id_povoleni_vjezdu_vozidla);
CREATE INDEX ix_povoleni_vjezdu_vozidla_lokalita_id_lokalita ON vratnice.povoleni_vjezdu_vozidla_lokalita (id_lokalita);


-- Úprava společnosti žadatele
ALTER TABLE vratnice.povoleni_vjezdu_vozidla 
    DROP COLUMN spolecnost_zadatele;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN spolecnost_zadatele VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_spolecnost_zadatele
	FOREIGN KEY (spolecnost_zadatele) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET spolecnost_zadatele = 'TKSP0000000001';


-- Úprava společnosti vozidla
ALTER TABLE vratnice.povoleni_vjezdu_vozidla 
    DROP COLUMN spolecnost_vozidla;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN spolecnost_vozidla VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_spolecnost_vozidla
	FOREIGN KEY (spolecnost_vozidla) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET spolecnost_vozidla = 'TKSP0000000001';

-- Přidání sloupce email
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN email_zadatele VARCHAR(255);

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET email_zadatele = 'test@gmail.com';


-- přidání sloupce id_zavod
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN id_zavod VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET id_zavod = 'XXZA0000000001';