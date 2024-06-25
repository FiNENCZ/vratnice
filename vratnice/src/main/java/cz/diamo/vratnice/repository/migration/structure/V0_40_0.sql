CREATE SEQUENCE vratnice.seq_vjezd_vozidla_id_vjezd_vozidla INCREMENT 1 START 1;

CREATE TABLE vratnice.vjezd_vozidla (
    id_vjezd_vozidla VARCHAR(14) NOT NULL,
    id_ridic VARCHAR(14) NOT NULL,
    rz_vozidla VARCHAR(30) NOT NULL,
    typ_vozidla VARCHAR(30) NOT NULL,
    opakovany_vjezd INTEGER,
    cas_prijezdu DATE NOT NULL,
    CONSTRAINT pk_vjezd_vozidla PRIMARY KEY (id_vjezd_vozidla),
    CONSTRAINT fk_vjezd_vozidla_id_ridic FOREIGN KEY (id_ridic) REFERENCES vratnice.ridic (id_ridic) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_vjezd_vozidla_id_ridic ON vratnice.vjezd_vozidla (id_ridic);
