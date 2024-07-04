-- ALTER TABLE vratnice.klic
-- Přidání sloupců poznamka, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.klic
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;

-- ALTER TABLE vratnice.vjezd_vozidla
-- Přidání sloupců poznamka, aktivita, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.vjezd_vozidla
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita bool DEFAULT true  NULL,
    ADD COLUMN cas_zmn timestamp(6)  NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;

-- ALTER TABLE vratnice.vyjezd_vozidla
-- Přidání sloupců poznamka, aktivita, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.vyjezd_vozidla
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita bool DEFAULT true NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;

-- ALTER TABLE vratnice.najemnik_navstevnicka_karta
-- Přidání sloupců poznamka, aktivita, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.najemnik_navstevnicka_karta
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita bool DEFAULT true NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;

-- ALTER TABLE vratnice.navstevni_listek
-- Přidání sloupců poznamka, aktivita, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.navstevni_listek
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita bool DEFAULT true NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;

-- ALTER TABLE vratnice.zadost_klic
-- Přidání sloupců poznamka, aktivita, cas_zmn, zmenu_provedl
ALTER TABLE vratnice.zadost_klic
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita bool DEFAULT true NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;
