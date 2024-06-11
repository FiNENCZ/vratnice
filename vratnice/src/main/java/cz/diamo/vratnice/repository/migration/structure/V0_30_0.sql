CREATE SEQUENCE vratnice.seq_sluzebni_vozidlo_id_sluzebni_vozidlo INCREMENT 1 START 1;

CREATE TABLE vratnice.sluzebni_vozidlo (
    id_sluzebni_vozidlo VARCHAR(14) NOT NULL,
    typ VARCHAR(50) NOT NULL,
    kategorie VARCHAR(50) NOT NULL,
    funkce VARCHAR(50),
    id_zavod VARCHAR(14),
    lokalita VARCHAR(50),
    stav VARCHAR(50) NOT NULL,
    datum_od DATE,
    CONSTRAINT pk_sluzebni_vozidlo PRIMARY KEY (id_sluzebni_vozidlo),
    CONSTRAINT fk_sluzebni_vozidlo_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_sluzebni_vozidlo_id_zavod ON vratnice.sluzebni_vozidlo (id_zavod);
