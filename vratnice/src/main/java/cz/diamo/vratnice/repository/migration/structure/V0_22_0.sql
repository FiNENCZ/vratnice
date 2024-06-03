-- Přidej nový sloupec pro dočasný uložení hodnoty sloupce "datum"
ALTER TABLE vratnice.historie_vypujcek
ADD COLUMN datum_temp TIMESTAMP;

-- Aktualizuj hodnoty sloupce "datum_temp" pomocí hodnot ze sloupce "datum"
UPDATE vratnice.historie_vypujcek
SET datum_temp = datum;

-- Odstraň sloupec "datum"
ALTER TABLE vratnice.historie_vypujcek
DROP COLUMN datum;

-- Přejmenuj sloupec "datum_temp" na "datum"
ALTER TABLE vratnice.historie_vypujcek
RENAME COLUMN datum_temp TO datum;
