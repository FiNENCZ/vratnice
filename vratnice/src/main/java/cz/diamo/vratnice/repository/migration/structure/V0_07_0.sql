DO $$
BEGIN
    -- Přidání sloupce, pokud neexistuje
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_schema = 'vratnice' 
                   AND table_name = 'povoleni_vjezdu_vozidla' 
                   AND column_name = 'datum_vytvoreni') THEN
        ALTER TABLE vratnice.povoleni_vjezdu_vozidla
        ADD COLUMN datum_vytvoreni DATE;
    END IF;

    -- Aktualizace hodnoty datum_vytvoreni na CURRENT_DATE, pokud je NULL
    UPDATE vratnice.povoleni_vjezdu_vozidla
    SET datum_vytvoreni = CURRENT_DATE
    WHERE datum_vytvoreni IS NULL;
END $$;
