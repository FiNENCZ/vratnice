-- 1. Odstranění starého sloupce
ALTER TABLE vratnice.vjezd_vozidla
    DROP COLUMN cas_prijezdu;

-- 2. Přidání nového sloupce s možností hodnoty NULL
ALTER TABLE vratnice.vjezd_vozidla
    ADD COLUMN cas_prijezdu TIMESTAMP WITH TIME ZONE;

-- 3. Aktualizace existujících záznamů novým časovým údajem
UPDATE vratnice.vjezd_vozidla
SET cas_prijezdu = NOW(); -- nebo jiná výchozí hodnota

-- 4. Nastavení nového sloupce na NOT NULL
ALTER TABLE vratnice.vjezd_vozidla
    ALTER COLUMN cas_prijezdu SET NOT NULL;
