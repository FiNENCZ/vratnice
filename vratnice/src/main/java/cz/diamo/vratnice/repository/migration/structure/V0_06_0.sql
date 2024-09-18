ALTER TABLE vratnice.povoleni_vjezdu_vozidla
ADD COLUMN datum_vytvoreni DATE;

UPDATE vratnice.povoleni_vjezdu_vozidla
SET datum_vytvoreni = CURRENT_DATE;
