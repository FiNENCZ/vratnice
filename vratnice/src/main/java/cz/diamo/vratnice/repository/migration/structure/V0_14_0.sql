-- Nejprve odstraňte cizí klíč, který odkazuje na id_navstevni_listek
ALTER TABLE vratnice.navsteva_uzivatel_stav
DROP CONSTRAINT fk_navsteva_uzivatel_stav_id_navstevni_listek;

-- Nyní můžete odstranit sloupec id_navstevni_listek
ALTER TABLE vratnice.navsteva_uzivatel_stav
DROP COLUMN id_navstevni_listek;
