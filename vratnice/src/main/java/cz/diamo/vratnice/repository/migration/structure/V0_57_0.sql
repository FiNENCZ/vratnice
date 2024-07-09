-- Editace tabulky klic

ALTER TABLE vratnice.klic 
    DROP COLUMN status,
    DROP COLUMN lokalita,
    DROP COLUMN budova,
    DROP COLUMN poschodi;

ALTER TABLE vratnice.klic
    ADD COLUMN id_lokalita VARCHAR(14),
    ADD COLUMN id_budova VARCHAR(14),
    ADD COLUMN id_poschodi VARCHAR(14);

ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_lokalita
	FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita (id_lokalita) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_budova
	FOREIGN KEY (id_budova) REFERENCES vratnice.budova (id_budova) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_poschodi
	FOREIGN KEY (id_poschodi) REFERENCES vratnice.poschodi (id_poschodi) ON DELETE No Action ON UPDATE No Action