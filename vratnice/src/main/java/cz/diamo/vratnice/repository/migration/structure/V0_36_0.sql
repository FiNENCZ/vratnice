CREATE SEQUENCE vratnice.seq_ridic_id_ridic INCREMENT 1 START 1;

-- Vytvoření tabulky ridic
CREATE TABLE vratnice.ridic (
    id_ridic VARCHAR(14) NOT NULL,
    jmeno VARCHAR(50) NOT NULL,
    prijmeni VARCHAR(50) NOT NULL,
    cislo_op VARCHAR(30) NOT NULL,
    firma VARCHAR(120),
    datum_pouceni DATE,

    CONSTRAINT pk_ridic PRIMARY KEY (id_ridic)
);
