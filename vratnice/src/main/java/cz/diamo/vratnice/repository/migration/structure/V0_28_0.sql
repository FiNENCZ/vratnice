-- Drop the existing sequence if it exists
DROP SEQUENCE IF EXISTS vratnice.seq_sluzebni_vozidlo_id_sluzebni_vozidlo;

-- Drop the existing table if it exists
DROP TABLE IF EXISTS vratnice.sluzebni_vozidlo;

-- Create the sequence for generating IDs
CREATE SEQUENCE vratnice.seq_sluzebni_vozidlo_id_sluzebni_vozidlo INCREMENT 1 START 1;

-- Create the new table with the specified schema
CREATE TABLE vratnice.sluzebni_vozidlo (
    id_sluzebni_vozidlo VARCHAR(14) NOT NULL,
    typ VARCHAR(50) NOT NULL,
    kategorie VARCHAR(50) NOT NULL,
    funkce VARCHAR(50),
    id_zavod VARCHAR(14),
    lokalita TEXT,
    stav VARCHAR(255) NOT NULL,
    datum_od TIMESTAMP,
    CONSTRAINT pk_sluzebni_vozidlo PRIMARY KEY (id_sluzebni_vozidlo),
    CONSTRAINT fk_sluzebni_vozidlo_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- Create an index on the id_zavod column
CREATE INDEX ix_sluzebni_vozidlo_id_zavod ON vratnice.sluzebni_vozidlo (id_zavod);
