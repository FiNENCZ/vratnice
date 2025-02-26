---- Přejmenování ID - PRIMARY KEY
-- Zrušení starého primárního klíče
ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
DROP CONSTRAINT pk_navstevni_listek_uzivatel_stav;

-- Přejmenování sloupce id_navsteva_uzivatel_stav na id_navstevni_listek_uzivatel_stav
ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
RENAME COLUMN id_navsteva_uzivatel_stav TO id_navstevni_listek_uzivatel_stav;

-- Vytvoření nového primárního klíče na přejmenovaném sloupci
ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
ADD CONSTRAINT pk_navstevni_listek_uzivatel_stav PRIMARY KEY (id_navstevni_listek_uzivatel_stav);
