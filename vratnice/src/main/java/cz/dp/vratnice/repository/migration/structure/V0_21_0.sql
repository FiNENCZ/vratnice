ALTER TABLE vratnice.navstevni_listek
ADD COLUMN cas_vytvoreni timestamp(6) NOT NULL;

ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
ADD COLUMN cas_zmn timestamp(6) NOT NULL;

ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
ADD COLUMN zmenu_provedl varchar(100) NULL;

ALTER TABLE vratnice.navstevni_listek_uzivatel_stav
ADD COLUMN poznamka varchar(4000) NULL;