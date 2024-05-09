-- Údaje pro přihlášení do KC pro získání informací o uživatelých
ALTER TABLE vratnice.databaze ADD kc_uzivatele_jmeno VARCHAR(100);
ALTER TABLE vratnice.databaze ADD kc_uzivatele_heslo VARCHAR(100);

-- Tabulka pro evidenci zástupů
create table vratnice.zastup (
    guid VARCHAR(100) not null, 
    id_uzivatel VARCHAR(14) not null,
    id_uzivatel_zastupce VARCHAR(14) not null,
    platnost_od timestamp (6) not null,
    platnost_do timestamp (6) not null,
    distribuovano boolean not null default false,
    chyba_distribuce text,
    poznamka VARCHAR(4000),
    aktivita boolean not null default true,  
    cas_zmn timestamp (6) not null,
    zmenu_provedl VARCHAR(100), 
    constraint pk_zastup primary key (guid)   
);

ALTER TABLE vratnice.zastup ADD CONSTRAINT fk_zastup_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.zastup ADD CONSTRAINT fk_zastup_id_uzivatel_zastupce
	FOREIGN KEY (id_uzivatel_zastupce) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

-- Nová role pro správu účtu ke KC
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_KC_UZIVATELE','Správce - účet KeyCloak - uživatelé');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_KC_UZIVATELE', 'ROLE_SPRAVA_KC_UZIVATELE');

-- Nová role pro správu zástupů
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_ZASTUPU','Správce - zástupy');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_ZASTUPU', 'ROLE_SPRAVA_ZASTUPU');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE','Správce - zástupy - synchronizace');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE', 'ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE');

-- Na uživatele doplněn zástup pro aktuální zastupování
ALTER TABLE vratnice.uzivatel ADD id_zastup VARCHAR(14);

-- Na oprávnění doplněn příznak zda lze zastupovat
ALTER TABLE vratnice.opravneni ADD zastup boolean not null default true;

-- Aktualizace logování oprávnění
CREATE OR REPLACE FUNCTIon vratnice.opravneni_log() RETURNS trigger AS $opravneni_log$

    declare zmeny zmena[];
    declare zm zmena;
    declare zmenu_provedl_txt text;
    declare cas_zm timestamp;
    declare puvodni text;
    declare nova text;

    BEGIN

        IF(TG_OP = 'INSERT') THEN
            -- Změnu provedl dle uživatele
            select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = NEW.zmenu_provedl;
            cas_zm = now();
            -- Změnu provedl ext. uživ.
            IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = NEW.zmenu_provedl;
            END IF;

            insert into vratnice.historie_zaznamu (id_zaznamu, id_historie_zaznamu_typ, table_name, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_opravneni, 0, 'opravneni', zmenu_provedl_txt, cas_zm, NEW.zmenu_provedl);
        ELSIF (TG_OP = 'DELETE') THEN
           -- Změnu provedl dle uživatele
            select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = OLD.zmenu_provedl;
            cas_zm = now();
            -- Změnu provedl ext. uživ.
            IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = OLD.zmenu_provedl;
            END IF;

            insert into vratnice.historie_zaznamu (id_zaznamu, id_historie_zaznamu_typ, table_name, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_opravneni, 2, 'opravneni', zmenu_provedl_txt, cas_zm, OLD.zmenu_provedl);      
        ELSIF (TG_OP = 'UPDATE') THEN 
            IF(OLD.id_opravneni_typ_pristupu != NEW.id_opravneni_typ_pristupu)THEN 
                select nazev_resx from vratnice.opravneni_typ_pristupu where id_opravneni_typ_pristupu = OLD.id_opravneni_typ_pristupu into puvodni;
                select nazev_resx from vratnice.opravneni_typ_pristupu where id_opravneni_typ_pristupu = NEW.id_opravneni_typ_pristupu into nova;
                select 'id_opravneni_typ_pristupu', puvodni, nova into zm ;
                zmeny = array_append(zmeny, zm);
            END IF;       
            IF(coalesce(OLD.kod, '') != coalesce(NEW.kod, ''))THEN 
                select 'kod', OLD.kod, NEW.kod into zm;
                zmeny = array_append(zmeny, zm);
            END IF;    
            IF(coalesce(OLD.nazev, '') != coalesce(NEW.nazev, ''))THEN 
                select 'nazev', OLD.nazev, NEW.nazev into zm;
                zmeny = array_append(zmeny, zm);
            END IF;            
            IF(coalesce(OLD.poznamka, '') != coalesce(NEW.poznamka, ''))THEN 
                select 'poznamka', OLD.poznamka, NEW.poznamka into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(OLD.aktivita != NEW.aktivita)THEN 
                select 'aktivita', OLD.aktivita, NEW.aktivita into zm;
                zmeny = array_append(zmeny, zm);
            END IF;      
             IF(OLD.zastup != NEW.zastup)THEN 
                select 'zastup', OLD.zastup, NEW.zastup into zm;
                zmeny = array_append(zmeny, zm);
            END IF;      
            IF(array_length(zmeny, 1)>0)THEN
            
                -- Změnu provedl dle uživatele
               select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = NEW.zmenu_provedl;
               cas_zm = now();
               -- Změnu provedl ext. uživ.
               IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                    select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = NEW.zmenu_provedl;
               END IF;

                FOREACH zm IN ARRAY zmeny                             
                LOOP                                                    
                    insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, old_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                    values (NEW.id_opravneni, 'opravneni', zm.sloupec, zm.new_value, zm.old_value, zmenu_provedl_txt, cas_zm, NEW.zmenu_provedl);
                END LOOP;    
            END IF;
        END IF;

        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$opravneni_log$ LANGUAGE plpgsql;

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ZASTUP','Zástup');

-- Upravení logu
CREATE OR REPLACE FUNCTIon vratnice.uzivatel_log() RETURNS trigger AS $uzivatel_log$

    declare zmeny zmena[];
    declare zm zmena;
    declare zmenu_provedl_txt text;
    declare cas_zm timestamp;
    declare puvodni text;
    declare nova text;

    BEGIN

        IF(TG_OP = 'INSERT') THEN
            -- Změnu provedl dle uživatele
            select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = NEW.zmenu_provedl;
            cas_zm = now();
            -- Změnu provedl ext. uživ.
            IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = NEW.zmenu_provedl;
            END IF;

            insert into vratnice.historie_zaznamu (id_zaznamu, id_historie_zaznamu_typ, table_name, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_uzivatel, 0, 'uzivatel', zmenu_provedl_txt, cas_zm, NEW.zmenu_provedl);
        ELSIF (TG_OP = 'DELETE') THEN
            -- Změnu provedl dle uživatele
            select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = OLD.zmenu_provedl;
            cas_zm = now();
            -- Změnu provedl ext. uživ.
            IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = OLD.zmenu_provedl;
            END IF;

            insert into vratnice.historie_zaznamu (id_zaznamu, id_historie_zaznamu_typ, table_name, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_uzivatel, 2, 'uzivatel', zmenu_provedl_txt, cas_zm, OLD.zmenu_provedl); 
        ELSIF (TG_OP = 'UPDATE') THEN            
            IF(coalesce(OLD.nazev, '') != coalesce(NEW.nazev, ''))THEN 
                select 'nazev', OLD.nazev, NEW.nazev into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.jmeno, '') != coalesce(NEW.jmeno, ''))THEN 
                select 'jmeno', OLD.jmeno, NEW.jmeno into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.prijmeni, '') != coalesce(NEW.prijmeni, ''))THEN 
                select 'prijmeni', OLD.prijmeni, NEW.prijmeni into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.titul_pred, '') != coalesce(NEW.titul_pred, ''))THEN 
                select 'titul_pred', OLD.titul_pred, NEW.titul_pred into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.titul_za, '') != coalesce(NEW.titul_za, ''))THEN 
                select 'titul_za', OLD.titul_za, NEW.titul_za into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.email, '') != coalesce(NEW.email, ''))THEN 
                select 'email', OLD.email, NEW.email into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.soukromy_email, '') != coalesce(NEW.soukromy_email, ''))THEN 
                select 'soukromy_email', OLD.soukromy_email, NEW.soukromy_email into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.tel, '') != coalesce(NEW.tel, ''))THEN 
                select 'tel', OLD.tel, NEW.tel into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.poznamka, '') != coalesce(NEW.poznamka, ''))THEN 
                select 'poznamka', OLD.poznamka, NEW.poznamka into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(OLD.aktivita != NEW.aktivita)THEN 
                select 'aktivita', OLD.aktivita, NEW.aktivita into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
             IF(coalesce(OLD.datum_od,'1900-01-01') != coalesce(NEW.datum_od,'1900-01-01'))THEN 
                select 'datum_od', TO_CHAR(OLD.datum_od, 'DD.MM.YYYY'), TO_CHAR(NEW.datum_od, 'DD.MM.YYYY') into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.datum_do,'1900-01-01') != coalesce(NEW.datum_do,'1900-01-01'))THEN 
                select 'datum_do', TO_CHAR(OLD.datum_do, 'DD.MM.YYYY'), TO_CHAR(NEW.datum_do, 'DD.MM.YYYY') into zm;
                zmeny = array_append(zmeny, zm);
            END IF;            
            IF(coalesce(OLD.id_kmenova_data, '') != coalesce(NEW.id_kmenova_data, ''))THEN                
                select CONCAT  (id_kmenova_data, ' - dávka ', guid_davky) from vratnice.kmenova_data where id_kmenova_data = OLD.id_kmenova_data into puvodni;
                select CONCAT  (id_kmenova_data, ' - dávka ', guid_davky) from vratnice.kmenova_data where id_kmenova_data = NEW.id_kmenova_data into nova;
                select 'id_kmenova_data', puvodni, nova into zm ;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.platnost_ke_dni,'1900-01-01') != coalesce(NEW.platnost_ke_dni,'1900-01-01'))THEN 
                select 'platnost_ke_dni', TO_CHAR(OLD.platnost_ke_dni, 'DD.MM.YYYY'), TO_CHAR(NEW.platnost_ke_dni, 'DD.MM.YYYY') into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
             IF(coalesce(OLD.id_zastup, '') != coalesce(NEW.id_zastup, ''))THEN                 
                select CONCAT  (uzivatel.nazev, ' - ', uzivatel.sap_id) from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = OLD.id_zastup into puvodni;
                select CONCAT  (uzivatel.nazev, ' - ', uzivatel.sap_id) from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = NEW.id_zastup into nova;
                select 'id_zastup', puvodni, nova into zm ;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(array_length(zmeny, 1)>0)THEN
            
                -- Změnu provedl dle uživatele
               select uzivatel.nazev into zmenu_provedl_txt from vratnice.uzivatel uzivatel where uzivatel.id_uzivatel = NEW.zmenu_provedl;
               cas_zm = now();
               -- Změnu provedl ext. uživ.
               IF(coalesce(zmenu_provedl_txt, '') = '')THEN
                    select nazev into zmenu_provedl_txt from vratnice.externi_uzivatel where username = NEW.zmenu_provedl;
               END IF;

                FOREACH zm IN ARRAY zmeny                             
                LOOP                                                    
                    insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, old_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                    values (NEW.id_uzivatel, 'uzivatel', zm.sloupec, zm.new_value, zm.old_value, zmenu_provedl_txt, cas_zm, NEW.zmenu_provedl);
                END LOOP;    
            END IF;
        END IF;

        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$uzivatel_log$ LANGUAGE plpgsql;

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_ZASTUP','Zástup');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 8, cas_zmn = now(), zmenu_provedl = 'pgadmin';