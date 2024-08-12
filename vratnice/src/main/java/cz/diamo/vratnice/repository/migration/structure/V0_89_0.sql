CREATE SEQUENCE vratnice.seq_spolecnost_id_spolecnost INCREMENT 1 START 1;

CREATE TABLE vratnice.spolecnost (
    id_spolecnost VARCHAR(14) NOT NULL,
    nazev VARCHAR(80) NOT NULL,
    CONSTRAINT pk_spolecnost PRIMARY KEY (id_spolecnost)
);
