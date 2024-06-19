CREATE SEQUENCE vratnice.seq_povoleni_vjezdu_vozidla_id_povoleni_vjezdu_vozidla INCREMENT 1 START 1;

-- Vytvoření tabulky PovoleniVjezduVozidla
CREATE TABLE IF NOT EXISTS vratnice.povoleni_vjezdu_vozidla (
    id_povoleni_vjezdu_vozidla VARCHAR(14) NOT NULL,
    jmeno_zadatele VARCHAR(30) NOT NULL,
    prijmeni_zadatele VARCHAR(30) NOT NULL,
    spolecnost_zadatele VARCHAR(120) NOT NULL,
    ico_zadatele VARCHAR(50),
    duvod_zadosti VARCHAR(255),
    rz_vozidla JSONB, -- JSONB pro dynamické ukládání seznamu RZ vozidel
    typ_vozidla JSONB, -- JSONB pro dynamické ukládání seznamu typů vozidel
    zeme_registrace_vozidla VARCHAR(100) NOT NULL,
    id_ridic VARCHAR(14), -- Odkaz na Ridic entity
    spolecnost_vozidla VARCHAR(255),
    datum_od TIMESTAMP NOT NULL,
    datum_do TIMESTAMP NOT NULL,
    opakovany_vjezd BOOLEAN,
    stav VARCHAR(50) DEFAULT 'vyžádáno',
    CONSTRAINT pk_povoleni_vjezdu_vozidla PRIMARY KEY (id_povoleni_vjezdu_vozidla),
    CONSTRAINT fk_povoleni_vjezdu_vozidla_id_ridic FOREIGN KEY (id_ridic) REFERENCES vratnice.ridic (id_ridic) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_id_ridic ON vratnice.povoleni_vjezdu_vozidla (id_ridic);

-- Vytvoření tabulky povoleni_vjezdu_vozidla_zavod pro Many-to-Many vztah s entitou Zavod
CREATE TABLE IF NOT EXISTS vratnice.povoleni_vjezdu_vozidla_zavod (
    id_povoleni_vjezdu_vozidla VARCHAR(14) NOT NULL,
    id_zavod VARCHAR(14) NOT NULL,
    CONSTRAINT pk_povoleni_vjezdu_vozidla_zavod  PRIMARY KEY (id_povoleni_vjezdu_vozidla, id_zavod),
    CONSTRAINT fk_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_povoleni_vjezdu_vozidla_zavod_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_zavod (id_povoleni_vjezdu_vozidla);
CREATE INDEX ix_povoleni_vjezdu_vozidla_zavod_id_zavod ON vratnice.povoleni_vjezdu_vozidla_zavod (id_zavod);