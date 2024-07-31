ALTER TABLE vratnice.vjezd_vozidla
    ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.vjezd_vozidla ADD CONSTRAINT fk_vjezd_vozidla_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE vratnice.vyjezd_vozidla
    ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.vyjezd_vozidla ADD CONSTRAINT fk_vyjezd_vozidla_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action
    