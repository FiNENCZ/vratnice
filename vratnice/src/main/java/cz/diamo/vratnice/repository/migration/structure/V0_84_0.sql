ALTER TABLE vratnice.navstevni_listek
ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.navstevni_listek ADD CONSTRAINT fk_navstevni_listek_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.navstevni_listek
    SET id_vratnice = 'TKSU0000000001';