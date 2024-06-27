-- Zrušení indexů pro tabulku navstevni_listek_uzivatel
DROP INDEX IF EXISTS vratnice.ix_navstevni_listek_uzivatel_id_navstevni_listek;
DROP INDEX IF EXISTS vratnice.ix_navstevni_listek_uzivatel_id_uzivatel;

-- Zrušení tabulky navstevni_listek_uzivatel
DROP TABLE IF EXISTS vratnice.navstevni_listek_uzivatel CASCADE;