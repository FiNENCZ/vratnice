ALTER TABLE vratnice.najemnik_navstevnicka_karta 
    DROP COLUMN spolecnost;


ALTER TABLE vratnice.najemnik_navstevnicka_karta
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.najemnik_navstevnicka_karta ADD CONSTRAINT fk_najemnik_navstevnicka_karta_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.najemnik_navstevnicka_karta
    SET id_spolecnost = 'TKSP0000000001';