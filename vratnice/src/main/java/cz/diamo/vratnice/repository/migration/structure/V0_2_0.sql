-- Tabulka uživatel - závod (pro možný přístup k ostatním závodům)
create table vratnice.uzivatel_zavod (
     id_uzivatel VARCHAR(14) not null,
     id_zavod VARCHAR(14) not null, 
     constraint pk_uzivatel_zavod primary key (id_uzivatel, id_zavod)   
);

ALTER TABLE vratnice.uzivatel_zavod ADD CONSTRAINT fk_uzivatel_zavod_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.uzivatel_zavod ADD CONSTRAINT fk_uzivatel_zavod_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
;

-- Nastavení posledního použitého závodu
ALTER TABLE vratnice.uzivatel ADD id_zavod_vyber varchar(14);

ALTER TABLE vratnice.uzivatel ADD CONSTRAINT fk_uzivatel_id_zavod_vyber
	FOREIGN KEY (id_zavod_vyber) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
;

-- Logování uživatel-oprávnění
CREATE OR REPLACE FUNCTIon vratnice.uzivatel_zavod_log() RETURNS trigger AS $uzivatel_zavod_log$

    declare zmeny zmena[];
    declare zm zmena;
    declare zmenu_provedl_txt text;
    declare cas_zm timestamp;
    declare puvodni text;
    declare nova text;

    BEGIN

        IF(TG_OP = 'INSERT') THEN
            zmenu_provedl_txt = '';
            cas_zm = now();
            
            select CONCAT ( 'Přidán přístup k závodu ', (select nazev from vratnice.zavod where id_zavod = NEW.id_zavod)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_uzivatel, 'uzivatel', 'id_zavod', nova, zmenu_provedl_txt, cas_zm, '');
           
        ELSIF (TG_OP = 'DELETE') THEN
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Odebrán přístup k závodu ', (select nazev from vratnice.zavod where id_zavod = OLD.id_zavod)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_uzivatel, 'uzivatel', 'id_zavod', puvodni, zmenu_provedl_txt, cas_zm, '');
        ELSIF (TG_OP = 'UPDATE') THEN   
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Přidán přístup k závodu ', (select nazev from vratnice.zavod where id_zavod = NEW.id_zavod)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_uzivatel, 'uzivatel', 'id_zavod', nova, zmenu_provedl_txt, cas_zm, '');
            select CONCAT ( 'Odebrán přístup k závodu ', (select nazev from vratnice.zavod where id_zavod = OLD.id_zavod)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_uzivatel, 'uzivatel', 'id_zavod', puvodni, zmenu_provedl_txt, cas_zm, '');
           
        END IF;
        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$uzivatel_zavod_log$ LANGUAGE plpgsql;


-- Trigger pro zakládání logu
CREATE TRIGGER uzivatel_zavod_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.uzivatel_zavod
    FOR EACH ROW EXECUTE PROCEDURE uzivatel_zavod_log();


-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 2, cas_zmn = now(), zmenu_provedl = 'pgadmin';