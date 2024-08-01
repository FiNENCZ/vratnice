ALTER TABLE vratnice.klic
ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.klic
    SET id_vratnice = 'TKSU0000000001';