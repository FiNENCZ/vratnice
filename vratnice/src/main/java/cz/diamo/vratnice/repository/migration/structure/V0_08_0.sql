-- Změna stavu žádosti
CREATE SEQUENCE vratnice.seq_povoleni_vjezdu_vozidla_zmena_stavu_id_povoleni_vjezdu_vozidla_zmena_stavu INCREMENT 1 START 1;
CREATE TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu (
    id_povoleni_vjezdu_vozidla_zmena_stavu bigint NOT NULL DEFAULT NEXTVAL(('vratnice.seq_povoleni_vjezdu_vozidla_zmena_stavu_id_povoleni_vjezdu_vozidla_zmena_stavu'::text)::regclass),
    id_povoleni_vjezdu_vozidla varchar(14) NOT NULL,
    id_povoleni_vjezdu_vozidla_stav_novy INTEGER not null,
    id_povoleni_vjezdu_vozidla_stav_puvodni INTEGER not null,
    id_uzivatel varchar(14) NOT NULL,
    cas timestamp(6) NOT NULL,
	CONSTRAINT pk_povoleni_vjezdu_vozidla_zmena_stavu PRIMARY KEY (id_povoleni_vjezdu_vozidla_zmena_stavu)
);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zmena_stavu_id_povoleni
	FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zmena_stavu_id_povoleni_stav_novy
	FOREIGN KEY (id_povoleni_vjezdu_vozidla_stav_novy) REFERENCES vratnice.zadost_stav (id_zadost_stav) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zmena_stavu_id_povoleni_stav_puvodni
	FOREIGN KEY (id_povoleni_vjezdu_vozidla_stav_puvodni) REFERENCES vratnice.zadost_stav (id_zadost_stav) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zmena_stavu_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

-- Automatické zakládání změny stavu
CREATE OR REPLACE FUNCTIon vratnice.povoleni_vjezdu_vozidla_zmena_stavu_log() RETURNS trigger AS $povoleni_vjezdu_vozidla_zmena_stavu_log$
    
    BEGIN

        IF (TG_OP = 'UPDATE' AND OLD.id_zadost_stav != NEW.id_zadost_stav) THEN  
            insert into vratnice.povoleni_vjezdu_vozidla_zmena_stavu (id_povoleni_vjezdu_vozidla, id_povoleni_vjezdu_vozidla_stav_novy, id_povoleni_vjezdu_vozidla_stav_puvodni, id_uzivatel, cas)
            values (NEW.id_zadost, NEW.id_zadost_stav, OLD.id_zadost_stav, NEW.zmenu_provedl, NEW.cas_zmn);            
        END IF;

        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$povoleni_vjezdu_vozidla_zmena_stavu_log$ LANGUAGE plpgsql;

CREATE TRIGGER povoleni_vjezdu_vozidla_zmena_stavu_log_trg AFTER UPDATE on vratnice.povoleni_vjezdu_vozidla
    FOR EACH ROW EXECUTE PROCEDURE povoleni_vjezdu_vozidla_zmena_stavu_log();