
DROP INDEX vratnice.ix_historie_sluzebni_auto_id_sluzebni_vozidlo;
DROP INDEX vratnice.ix_historie_sluzebni_auto_id_uzivatel;
DROP TABLE vratnice.historie_sluzebni_auto;
DROP SEQUENCE vratnice.seq_historie_sluzebni_auto_id_historie_sluzebni_auto;


CREATE SEQUENCE vratnice.seq_historie_sluzebni_vozidlo_id_historie_sluzebni_vozidlo INCREMENT 1 START 1;

CREATE TABLE vratnice.historie_sluzebni_vozidlo (
    id_historie_sluzebni_vozidlo VARCHAR(14) NOT NULL,
    id_sluzebni_vozidlo VARCHAR(14) NOT NULL,
    akce VARCHAR(30) NOT NULL,
    datum TIMESTAMP NOT NULL,
    id_uzivatel VARCHAR(14),
    CONSTRAINT pk_historie_sluzebni_vozidlo PRIMARY KEY (id_historie_sluzebni_vozidlo),
    CONSTRAINT fk_historie_sluzebni_vozidlo_id_sluzebni_vozidlo FOREIGN KEY (id_sluzebni_vozidlo) REFERENCES vratnice.sluzebni_vozidlo (id_sluzebni_vozidlo) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_historie_sluzebni_vozidlo_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_historie_sluzebni_vozidlo_id_sluzebni_vozidlo ON vratnice.historie_sluzebni_vozidlo (id_sluzebni_vozidlo);
CREATE INDEX ix_historie_sluzebni_vozidlo_id_uzivatel ON vratnice.historie_sluzebni_vozidlo (id_uzivatel);