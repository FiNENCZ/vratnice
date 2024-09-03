CREATE SEQUENCE vratnice.seq_specialni_klic_oznameni_vypujcky_id_specialni_klic_oznameni_vypujcky INCREMENT 1 START 1;

-- Vytvoření tabulky NavstevniListek
CREATE TABLE IF NOT EXISTS vratnice.specialni_klic_oznameni_vypujcky (
    id_specialni_klic_oznameni_vypujcky VARCHAR(14) NOT NULL,
    id_klic varchar(14) NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_specialni_klic_oznameni_vypujcky PRIMARY KEY (id_specialni_klic_oznameni_vypujcky),
    CONSTRAINT fk_specialni_klic_oznameni_vypujcky_id_klic FOREIGN KEY (id_klic) REFERENCES vratnice.klic(id_klic) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT uq_specialni_klic_oznameni_vypujcky_id_klic UNIQUE (id_klic)
);

CREATE INDEX ix_specialni_klic_oznameni_vypujcky_id_klic ON vratnice.klic (id_klic);

CREATE TABLE IF NOT EXISTS vratnice.specialni_klic_oznameni_uzivatel (
    id_specialni_klic_oznameni_vypujcky VARCHAR(14) NOT NULL,
    id_uzivatel VARCHAR(14) NOT NULL,
    CONSTRAINT pk_specialni_klic_oznameni_uzivatel  PRIMARY KEY (id_specialni_klic_oznameni_vypujcky, id_uzivatel),
    CONSTRAINT fk_specialni_klic_oznameni_uzivatel_id_specialni_klic_oznameni_vypujcky FOREIGN KEY (id_specialni_klic_oznameni_vypujcky) REFERENCES vratnice.specialni_klic_oznameni_vypujcky  (id_specialni_klic_oznameni_vypujcky) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_specialni_klic_oznameni_uzivatel_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_specialni_klic_oznameni_uzivatel_id_specialni_klic_oznameni_vypujcky ON vratnice.specialni_klic_oznameni_uzivatel (id_specialni_klic_oznameni_vypujcky);
CREATE INDEX ix_specialni_klic_oznameni_uzivatel_id_uzivatel ON vratnice.specialni_klic_oznameni_uzivatel (id_uzivatel);
