CREATE SEQUENCE vratnice.seq_inicializace_vratnice_kamery_id_inicializace_vratnice_kamery INCREMENT 1 START 1;

-- Vytvoření tabulky inicializace_vratnice_kamery
CREATE TABLE vratnice.inicializace_vratnice_kamery (
    id_inicializace_vratnice_kamery VARCHAR(14) NOT NULL,
    ip_adresa VARCHAR(20) NOT NULL,
    cas_inicializace TIMESTAMP NOT NULL,

    CONSTRAINT pk_inicializace_vratnice_kamery PRIMARY KEY (id_inicializace_vratnice_kamery)
);
