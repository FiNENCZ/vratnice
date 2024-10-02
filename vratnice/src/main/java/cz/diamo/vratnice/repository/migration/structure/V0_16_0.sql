-- Přidání nového sloupce id_navstevni_listek s možností NULL
ALTER TABLE vratnice.navsteva_uzivatel_stav
ADD COLUMN id_navstevni_listek varchar(14);

-- Nastavení hodnoty "TKNL0000000006" pro každý existující záznam
UPDATE vratnice.navsteva_uzivatel_stav
SET id_navstevni_listek = 'TKNL0000000006';

-- Změna sloupce id_navstevni_listek na NOT NULL
ALTER TABLE vratnice.navsteva_uzivatel_stav
ALTER COLUMN id_navstevni_listek SET NOT NULL;

-- Přidání cizího klíče pro sloupec id_navstevni_listek
ALTER TABLE vratnice.navsteva_uzivatel_stav
ADD CONSTRAINT fk_navsteva_uzivatel_stav_id_navstevni_listek FOREIGN KEY (id_navstevni_listek)
REFERENCES vratnice.navstevni_listek(id_navstevni_listek);

-- Vytvoření indexu na sloupci id_navstevni_listek
CREATE INDEX ix_navsteva_uzivatel_stav_id_navstevni_listek 
ON vratnice.navsteva_uzivatel_stav USING btree (id_navstevni_listek);
