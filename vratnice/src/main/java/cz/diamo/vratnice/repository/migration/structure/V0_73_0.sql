-- Na uživatele doplněn cip1 a cip2
ALTER TABLE vratnice.uzivatel ADD cip_1 VARCHAR(100);
ALTER TABLE vratnice.uzivatel ADD cip_2 VARCHAR(100);

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
            IF(coalesce(OLD.ulice, '') != coalesce(NEW.ulice, ''))THEN 
                select 'ulice', OLD.ulice, NEW.ulice into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.psc, '') != coalesce(NEW.psc, ''))THEN 
                select 'psc', OLD.psc, NEW.psc into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.obec, '') != coalesce(NEW.obec, ''))THEN 
                select 'obec', OLD.obec, NEW.obec into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.cislo_popisne, '') != coalesce(NEW.cislo_popisne, ''))THEN 
                select 'cislo_popisne', OLD.cislo_popisne, NEW.cislo_popisne into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.dilci_personalni_oblast, '') != coalesce(NEW.dilci_personalni_oblast, ''))THEN 
                select 'dilci_personalni_oblast', OLD.dilci_personalni_oblast, NEW.dilci_personalni_oblast into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.id_zakazka, '') != coalesce(NEW.id_zakazka, ''))THEN                 
                select zakazka.sap_id from vratnice.zakazka zakazka where zakazka.id_zakazka = OLD.id_zakazka into puvodni;
                select zakazka.sap_id from vratnice.zakazka zakazka where zakazka.id_zakazka = NEW.id_zakazka into nova;
                select 'id_zakazka', puvodni, nova into zm ;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(OLD.pruzna_prac_doba != NEW.pruzna_prac_doba)THEN 
                select 'pruzna_prac_doba', OLD.pruzna_prac_doba, NEW.pruzna_prac_doba into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.cip_1, '') != coalesce(NEW.cip_1, ''))THEN 
                select 'cip_1', OLD.cip_1, NEW.cip_1 into zm;
                zmeny = array_append(zmeny, zm);
            END IF;
            IF(coalesce(OLD.cip_2, '') != coalesce(NEW.cip_2, ''))THEN 
                select 'cip_2', OLD.cip_2, NEW.cip_2 into zm;
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

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_CIP_1','Přístupvý čip');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_CIP_2','Přístupvý čip - sekundární');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 73, cas_zmn = now(), zmenu_provedl = 'pgadmin';