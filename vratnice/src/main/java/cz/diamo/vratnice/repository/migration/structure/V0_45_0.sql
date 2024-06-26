CREATE SEQUENCE vratnice.seq_vyjezd_vozidla_id START WITH 1 INCREMENT BY 1;

CREATE TABLE vratnice.vyjezd_vozidla (
    id_vyjezd_vozidla VARCHAR(14) NOT NULL,
    rz_vozidla VARCHAR(30) NOT NULL,
    naklad BOOLEAN NOT NULL DEFAULT FALSE,
    cislo_pruchodky VARCHAR(30),
    opakovany_vjezd BOOLEAN,
    cas_odjezdu TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_vyjezd_vozidla PRIMARY KEY (id_vyjezd_vozidla)
);