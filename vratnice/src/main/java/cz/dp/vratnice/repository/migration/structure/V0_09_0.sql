DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns 
        WHERE table_name='povoleni_vjezdu_vozidla_zmena_stavu' 
        AND column_name='aktivita_novy'
    ) THEN
        ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu
        ADD COLUMN aktivita_novy BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns 
        WHERE table_name='povoleni_vjezdu_vozidla_zmena_stavu' 
        AND column_name='aktivita_puvodni'
    ) THEN
        ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu
        ADD COLUMN aktivita_puvodni BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;
END $$;


CREATE OR REPLACE FUNCTION vratnice.povoleni_vjezdu_vozidla_zmena_stavu_log() RETURNS trigger AS $povoleni_vjezdu_vozidla_zmena_stavu_log$
    
BEGIN
    IF (TG_OP = 'UPDATE' AND (OLD.id_zadost_stav != NEW.id_zadost_stav OR OLD.aktivita != NEW.aktivita))THEN  
        IF NEW.zmenu_provedl IS NOT NULL THEN
            INSERT INTO vratnice.povoleni_vjezdu_vozidla_zmena_stavu (
                id_povoleni_vjezdu_vozidla, 
                id_povoleni_vjezdu_vozidla_stav_novy,
                aktivita_novy,
                id_uzivatel, 
                cas,
                id_povoleni_vjezdu_vozidla_stav_puvodni,
                aktivita_puvodni

            )
            VALUES (
                NEW.id_povoleni_vjezdu_vozidla, 
                NEW.id_zadost_stav,
                NEW.aktivita,
                NEW.zmenu_provedl,
                NEW.cas_zmn,
                OLD.id_zadost_stav,
                OLD.aktivita
            );  
        ELSE
            RAISE NOTICE 'NEW.zmenu_provedl is NULL, not inserting record.';
        END IF;
    END IF;

    IF (TG_OP = 'INSERT') THEN RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN RETURN NULL;
    END IF;
END;
$povoleni_vjezdu_vozidla_zmena_stavu_log$ LANGUAGE plpgsql;