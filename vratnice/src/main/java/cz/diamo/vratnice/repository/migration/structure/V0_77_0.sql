CREATE SEQUENCE vratnice.seq_uzivatel_vratnice_id_uzivatel_vratnice INCREMENT 1 START 1;

-- Vytvoření tabulky NavstevniListek
CREATE TABLE IF NOT EXISTS vratnice.uzivatel_vratnice (
    id_uzivatel_vratnice VARCHAR(14) NOT NULL,
    id_uzivatel varchar(14) NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_uzivatel_vratnice PRIMARY KEY (id_uzivatel_vratnice),
    CONSTRAINT fk_uzivatel_vratnice_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_uzivatel_vratnice_id_uzivatel ON vratnice.uzivatel (id_uzivatel);

-- Vytvoření tabulky uzivatel_vratnice_mapovani pro Many-to-Many vztah s entitou NavstevaOsoba
CREATE TABLE IF NOT EXISTS vratnice.uzivatel_vratnice_mapovani (
    id_uzivatel_vratnice VARCHAR(14) NOT NULL,
    id_vratnice VARCHAR(14) NOT NULL,
    CONSTRAINT pk_uzivatel_vratnice_mapovani  PRIMARY KEY (id_uzivatel_vratnice, id_vratnice),
    CONSTRAINT fk_uzivatel_vratnice_mapovani_id_uzivatel_vratnice FOREIGN KEY (id_uzivatel_vratnice) REFERENCES vratnice.uzivatel_vratnice (id_uzivatel_vratnice) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_uzivatel_vratnice_mapovani_id_vratnice FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_uzivatel_vratnice_mapovani_id_uzivatel_vratnice ON vratnice.uzivatel_vratnice_mapovani (id_uzivatel_vratnice);
CREATE INDEX ix_uzivatel_vratnice_mapovani_id_vratnice ON vratnice.uzivatel_vratnice_mapovani (id_vratnice);
