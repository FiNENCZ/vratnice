ALTER TABLE vratnice.sluzebni_vozidlo 
    DROP COLUMN lokalita;

-- Vytvoření tabulky sluzebni_vozidlo_lokalita pro Many-to-Many vztah s entitou lokalita
CREATE TABLE IF NOT EXISTS vratnice.sluzebni_vozidlo_lokalita (
    id_sluzebni_vozidlo VARCHAR(14) NOT NULL,
    id_lokalita VARCHAR(14) NOT NULL,
    CONSTRAINT pk_sluzebni_vozidlo_lokalita  PRIMARY KEY (id_sluzebni_vozidlo, id_lokalita),
    CONSTRAINT fk_sluzebni_vozidlo_lokalita_id_sluzebni_vozidlo FOREIGN KEY (id_sluzebni_vozidlo) REFERENCES vratnice.sluzebni_vozidlo (id_sluzebni_vozidlo) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_sluzebni_vozidlo_lokalita_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita (id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_sluzebni_vozidlo_lokalita_id_sluzebni_vozidlo ON vratnice.sluzebni_vozidlo_lokalita (id_sluzebni_vozidlo);
CREATE INDEX ix_sluzebni_vozidlo_lokalita_id_lokalita ON vratnice.sluzebni_vozidlo_lokalita (id_lokalita);