ALTER TABLE vratnice.ridic 
    DROP COLUMN firma;


ALTER TABLE vratnice.ridic
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.ridic ADD CONSTRAINT fk_ridic_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.ridic
    SET id_spolecnost = 'TKSP0000000001';


ALTER TABLE vratnice.navsteva_osoba 
    DROP COLUMN firma;

ALTER TABLE vratnice.navsteva_osoba
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.navsteva_osoba ADD CONSTRAINT fk_navsteva_osoba_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.navsteva_osoba
    SET id_spolecnost = 'TKSP0000000001';