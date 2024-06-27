CREATE SEQUENCE vratnice.seq_navsteva_osoba_id_navsteva_osoba INCREMENT 1 START 1;

CREATE TABLE vratnice.navsteva_osoba (
    id_navsteva_osoba VARCHAR(14) NOT NULL,
    jmeno VARCHAR(50) NOT NULL,
    prijmeni VARCHAR(50) NOT NULL,
    cislo_op VARCHAR(30) NOT NULL,
    firma VARCHAR(120),
    datum_pouceni DATE,
    CONSTRAINT pk_navsteva_osoba PRIMARY KEY (id_navsteva_osoba)
);

CREATE SEQUENCE vratnice.seq_navstevni_listek_id_navstevni_listek INCREMENT 1 START 1;

-- Vytvoření tabulky NavstevniListek
CREATE TABLE IF NOT EXISTS vratnice.navstevni_listek (
    id_navstevni_listek VARCHAR(14) NOT NULL,
    stav VARCHAR(30) NOT NULL,
    CONSTRAINT pk_navstevni_listek PRIMARY KEY (id_navstevni_listek)
);


-- Vytvoření tabulky navstevni_listek_navsteva_osoba pro Many-to-Many vztah s entitou NavstevaOsoba
CREATE TABLE IF NOT EXISTS vratnice.navstevni_listek_navsteva_osoba (
    id_navstevni_listek VARCHAR(14) NOT NULL,
    id_navsteva_osoba VARCHAR(14) NOT NULL,
    CONSTRAINT pk_navstevni_listek_navsteva_osoba  PRIMARY KEY (id_navstevni_listek, id_navsteva_osoba ),
    CONSTRAINT fk_navstevni_listek_navsteva_osoba_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek (id_navstevni_listek) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_navstevni_listek_navsteva_osoba_id_navsteva_osoba FOREIGN KEY (id_navsteva_osoba) REFERENCES vratnice.navsteva_osoba (id_navsteva_osoba) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_navstevni_listek_navsteva_osoba_id_navstevni_listek ON vratnice.navstevni_listek_navsteva_osoba (id_navstevni_listek);
CREATE INDEX ix_navstevni_listek_navsteva_osoba_id_navsteva_osoba ON vratnice.navstevni_listek_navsteva_osoba (id_navsteva_osoba);

-- Vytvoření tabulky navstevni_listek_uzivatel pro Many-to-Many vztah s entitou Uzivatel
CREATE TABLE IF NOT EXISTS vratnice.navstevni_listek_uzivatel (
    id_navstevni_listek VARCHAR(14) NOT NULL,
    id_uzivatel VARCHAR(14) NOT NULL,
    CONSTRAINT pk_navstevni_listek_uzivatel  PRIMARY KEY (id_navstevni_listek, id_uzivatel ),
    CONSTRAINT fk_navstevni_listek_uzivatel_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek (id_navstevni_listek) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_navstevni_listek_uzivatel_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_navstevni_listek_uzivatel_id_navstevni_listek ON vratnice.navstevni_listek_uzivatel (id_navstevni_listek);
CREATE INDEX ix_navstevni_listek_uzivatel_id_uzivatel ON vratnice.navstevni_listek_uzivatel (id_uzivatel);