CREATE TYPE vratnice.zmena AS (
	sloupec text,
	old_value text,
	new_value text
);

CREATE FUNCTION vratnice.opravneni_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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
$$;

CREATE FUNCTION vratnice.opravneni_pracovni_pozice_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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
                       
            select CONCAT ( 'Přidána pracovní pozice ', (select CONCAT(sap_id, ' - ', nazev) from vratnice.pracovni_pozice where id_pracovni_pozice = NEW.id_pracovni_pozice)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_opravneni, 'opravneni', 'id_pracovni_pozice', nova, zmenu_provedl_txt, cas_zm, '');
           
        ELSIF (TG_OP = 'DELETE') THEN
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Odebrána pracovní pozice ', (select CONCAT(sap_id, ' - ', nazev) from vratnice.pracovni_pozice where id_pracovni_pozice = OLD.id_pracovni_pozice)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_opravneni, 'opravneni', 'id_pracovni_pozice', puvodni, zmenu_provedl_txt, cas_zm, '');
        ELSIF (TG_OP = 'UPDATE') THEN   
            -- Změnu provedl dle profilu
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Přidána pracovní pozice ', (select CONCAT(sap_id, ' - ', nazev) from vratnice.pracovni_pozice where id_pracovni_pozice = NEW.id_pracovni_pozice)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_opravneni, 'opravneni', 'id_pracovni_pozice', nova, zmenu_provedl_txt, cas_zm, '');
            select CONCAT ( 'Odebrána pracovní pozice ', (select CONCAT(sap_id, ' - ', nazev) from vratnice.pracovni_pozice where id_pracovni_pozice = OLD.id_pracovni_pozice)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_opravneni, 'opravneni', 'id_pracovni_pozice', puvodni, zmenu_provedl_txt, cas_zm, '');
        END IF;
        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$$;

CREATE FUNCTION vratnice.opravneni_role_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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

                     
            select CONCAT ( 'Přidána role ', (select nazev_resx from vratnice.role where authority = NEW.authority)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_opravneni, 'opravneni', 'authority', nova, zmenu_provedl_txt, cas_zm, '');
           
        ELSIF (TG_OP = 'DELETE') THEN
             zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Odebrána role ', (select nazev_resx from vratnice.role where authority = OLD.authority)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_opravneni, 'opravneni', 'authority', puvodni, zmenu_provedl_txt, cas_zm, '');
        ELSIF (TG_OP = 'UPDATE') THEN   
            -- Změnu provedl dle profilu
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Přidána role ', (select nazev_resx from vratnice.role where authority = NEW.authority)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_opravneni, 'opravneni', 'authority', nova, zmenu_provedl_txt, cas_zm, '');
            select CONCAT ( 'Odebrána role ', (select nazev_resx from vratnice.role where authority = OLD.authority)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_opravneni, 'opravneni', 'authority', puvodni, zmenu_provedl_txt, cas_zm, '');
        END IF;
        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$$;

CREATE FUNCTION vratnice.opravneni_zavod_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

    declare zmeny zmena[];
    declare zm zmena;
    declare zmenu_provedl_txt text;
    declare cas_zm timestamp;
    declare puvodni text;
    declare nova text;

    BEGIN

        IF (TG_OP = 'DELETE') THEN
            select nazev from vratnice.zavod where id_zavod = OLD.id_zavod into puvodni;
            select 'id_zavod', puvodni, '' into zm;
            zmeny = array_append(zmeny, zm);
        ELSIF (TG_OP = 'INSERT') THEN
            select nazev from vratnice.zavod where id_zavod = NEW.id_zavod into nova;
            select 'id_zavod', '', nova into zm;
            zmeny = array_append(zmeny, zm);
        END IF;

        IF(array_length(zmeny, 1)>0)THEN
            cas_zm=now();
            FOREACH zm IN ARRAY zmeny                             
            LOOP        
                IF (TG_OP = 'DELETE') THEN                                            
                    insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, old_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                    values (OLD.id_opravneni, 'opravneni', zm.sloupec, zm.new_value, zm.old_value, '', cas_zm, '');
                ELSIF (TG_OP = 'INSERT') THEN
                    insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, old_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                    values (NEW.id_opravneni, 'opravneni', zm.sloupec, zm.new_value, zm.old_value, '', cas_zm, '');
                END IF;
            END LOOP;    
        END IF;
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN OLD;
        END IF;
    END;
$$;

CREATE FUNCTION vratnice.uzivatel_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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
$$;

CREATE FUNCTION vratnice.uzivatel_opravneni_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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
            
            select CONCAT ( 'Přidáno oprávnění ', (select nazev from vratnice.opravneni where id_opravneni = NEW.id_opravneni)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_uzivatel, 'uzivatel', 'id_opravneni', nova, zmenu_provedl_txt, cas_zm, '');
           
        ELSIF (TG_OP = 'DELETE') THEN
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Odebráno oprávnění ', (select nazev from vratnice.opravneni where id_opravneni = OLD.id_opravneni)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_uzivatel, 'uzivatel', 'id_opravneni', puvodni, zmenu_provedl_txt, cas_zm, '');
        ELSIF (TG_OP = 'UPDATE') THEN   
            zmenu_provedl_txt = '';
            cas_zm = now();
            select CONCAT ( 'Přidáno oprávnění ', (select nazev from vratnice.opravneni where id_opravneni = NEW.id_opravneni)) into nova; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (NEW.id_uzivatel, 'uzivatel', 'id_opravneni', nova, zmenu_provedl_txt, cas_zm, '');
            select CONCAT ( 'Odebráno oprávnění ', (select nazev from vratnice.opravneni where id_opravneni = OLD.id_opravneni)) into puvodni; 
            insert into vratnice.historie_zaznamu (id_zaznamu, table_name, table_column, new_value, zmenu_provedl_txt, cas_zmn, zmenu_provedl)
                values (OLD.id_uzivatel, 'uzivatel', 'id_opravneni', puvodni, zmenu_provedl_txt, cas_zm, '');
           
        END IF;
        
        IF (TG_OP = 'INSERT') THEN RETURN NEW;
        ELSIF (TG_OP = 'UPDATE') THEN RETURN NEW;
        ELSIF (TG_OP = 'DELETE') THEN  RETURN NULL;
        END IF;
    END;
$$;

CREATE FUNCTION vratnice.uzivatel_zavod_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

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
$$;

CREATE TABLE vratnice.budova (
    id_budova character varying(14) NOT NULL,
	id_externi varchar(14),
    nazev character varying(80) NOT NULL,
    id_lokalita character varying(14) NOT NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.databaze (
    id_databaze integer NOT NULL,
    db_prefix character varying(2) NOT NULL,
    verze_db smallint NOT NULL,
    sub_verze_db smallint NOT NULL,
    demo boolean DEFAULT false NOT NULL,
    poznamka character varying(4000),
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    kc_uzivatele_jmeno character varying(100),
    kc_uzivatele_heslo character varying(100)
);

insert into vratnice.databaze (id_databaze, db_prefix, verze_db, sub_verze_db, cas_zmn, zmenu_provedl) 
	values (0, 'TK', 0, 1, now(),'pgadmin');

CREATE TABLE vratnice.externi_role (
    authority character varying(100) NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.externi_uzivatel (
    id_externi_uzivatel character varying(14) NOT NULL,
    nazev character varying(1000) NOT NULL,
    username character varying(100) NOT NULL,
    password character varying(100) NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.externi_uzivatel_role (
    id_externi_uzivatel character varying(14) NOT NULL,
    authority character varying(100) NOT NULL
);

CREATE TABLE vratnice.historie_sluzebni_vozidlo (
    id_historie_sluzebni_vozidlo character varying(14) NOT NULL,
    id_sluzebni_vozidlo character varying(14) NOT NULL,
    datum timestamp without time zone NOT NULL,
    id_uzivatel character varying(14),
    id_historie_sluzebni_vozidlo_akce integer
);

CREATE TABLE vratnice.historie_sluzebni_vozidlo_akce (
    id_historie_sluzebni_vozidlo_akce integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.historie_vypujcek (
    id_historie_vypujcek character varying(14) NOT NULL,
    id_zadost_klic character varying(14) NOT NULL,
    datum timestamp without time zone,
    id_vratny character varying(14),
    id_historie_vypujcek_akce integer
);

CREATE TABLE vratnice.historie_vypujcek_akce (
    id_historie_vypujcek_akce integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.historie_zaznamu (
    id_historie_zaznamu bigint DEFAULT nextval(('vratnice.seq_historie_zaznamu_id_historie_zaznamu'::text)::regclass) NOT NULL,
    id_historie_zaznamu_typ integer DEFAULT 1 NOT NULL,
    id_zaznamu character varying(14) NOT NULL,
    table_name character varying(100) NOT NULL,
    table_column character varying(100),
    new_value character varying(1000),
    old_value character varying(1000),
    zmenu_provedl character varying(100),
    zmenu_provedl_txt character varying(1000),
    cas_zmn timestamp(6) without time zone NOT NULL
);

CREATE TABLE vratnice.historie_zaznamu_typ (
    id_historie_zaznamu_typ integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.jmeno_korektura (
    id_jmeno_korektura character varying(14) NOT NULL,
    jmeno_vstup character varying(50) NOT NULL,
    korektura character varying(50) NOT NULL
);

CREATE TABLE vratnice.klic (
    id_klic character varying(14) NOT NULL,
    specialni boolean NOT NULL,
    nazev character varying(50) NOT NULL,
    kod_cipu character varying(50) NOT NULL,
    mistnost character varying(50) NOT NULL,
    aktivita boolean DEFAULT true NOT NULL,
    poznamka character varying(4000),
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100),
    id_klic_typ integer,
    id_lokalita character varying(14),
    id_budova character varying(14),
    id_poschodi character varying(14)
);

CREATE TABLE vratnice.klic_typ (
    id_klic_typ integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.kmenova_data (
    id_kmenova_data character varying(14) NOT NULL,
    guid_davky character varying(100) NOT NULL,
    id_zavod character varying(14) NOT NULL,
    sap_id character varying(100) NOT NULL,
    druh_vyneti_sap_id character varying(100),
    druh_prac_pomeru_sap_id character varying(100),
    forma_mzdy_sap_id character varying(100),
    kategorie_sap_id character varying(100),
    kalendar_sap_id character varying(100),
    zakazka_sap_id character varying(100),
    personalni_oblast_sap_id character varying(100),
    dilci_personalni_oblast_sap_id character varying(100),
    zauctovaci_okruh_sap_id character varying(100),
    orgnizacni_jednotka_sap_id character varying(100),
    planovane_misto_sap_id character varying(100),
    skupina_zamestnance_sap_id character varying(100),
    platnost_ke_dni date NOT NULL,
    cislo_znamky character varying(20),
    rodne_cislo character varying(11),
    jmeno character varying(100) NOT NULL,
    prijmeni character varying(100) NOT NULL,
    titul_pred character varying(100),
    titul_za character varying(100),
    ulice character varying(400),
    psc character varying(400),
    obec character varying(400),
    cislo_popisne character varying(100),
    tel character varying(1000),
    email character varying(1000),
    soukromy_email character varying(1000),
    datum_ukonceni_prac_pomeru date,
    denni_uvazek numeric(15,2) NOT NULL,
    narok_na_dovolenou numeric(15,2) NOT NULL,
    zbytek_dovolene_minuly_rok numeric(15,2),
    dodatkova_dovolena numeric(15,2),
    cerpani_dovolene numeric(15,2) NOT NULL,
    prumer_pro_nahrady numeric(15,2) NOT NULL,
    pruzna_prac_doba boolean DEFAULT false NOT NULL,
    zpracovano boolean DEFAULT false NOT NULL,
    chyba character varying(4000),
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    cip_1 character varying(100),
    cip_2 character varying(100)
);

CREATE TABLE vratnice.lokalita (
    id_lokalita character varying(14) NOT NULL,
	id_externi varchar(14),
    nazev character varying(80) NOT NULL,
    id_zavod character varying(14),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.najemnik_navstevnicka_karta (
    id_najemnik_navstevnicka_karta character varying(14) NOT NULL,
    jmeno character varying(50) NOT NULL,
    prijmeni character varying(50) NOT NULL,
    cislo_op character varying(30) NOT NULL,
    spolecnost character varying(120),
    cislo_najemni_smlouvy character varying(30),
    cislo_karty character varying(30) NOT NULL,
    duvod_vydani text,
    vydano_od date,
    vydano_do date,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true,
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.navsteva_osoba (
    id_navsteva_osoba character varying(14) NOT NULL,
    jmeno character varying(50) NOT NULL,
    prijmeni character varying(50) NOT NULL,
    cislo_op character varying(30) NOT NULL,
    firma character varying(120),
    datum_pouceni date
);

CREATE TABLE vratnice.navstevni_listek (
    id_navstevni_listek character varying(14) NOT NULL,
    stav character varying(30) NOT NULL,
    id_navstevni_listek_typ integer,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true,
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.navstevni_listek_navsteva_osoba (
    id_navstevni_listek character varying(14) NOT NULL,
    id_navsteva_osoba character varying(14) NOT NULL
);

CREATE TABLE vratnice.navstevni_listek_typ (
    id_navstevni_listek_typ integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.navstevni_listek_uzivatel (
    id_navstevni_listek character varying(14) NOT NULL,
    id_uzivatel character varying(14) NOT NULL
);

CREATE TABLE vratnice.opravneni (
    id_opravneni character varying(14) NOT NULL,
    id_opravneni_typ_pristupu integer NOT NULL,
    kod character varying(100) NOT NULL,
    nazev character varying(100) NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    zastup boolean DEFAULT true NOT NULL
);

CREATE TABLE vratnice.opravneni_pracovni_pozice (
    id_opravneni character varying(14) NOT NULL,
    id_pracovni_pozice character varying(100) NOT NULL
);

CREATE TABLE vratnice.opravneni_role (
    id_opravneni character varying(14) NOT NULL,
    authority character varying(100) NOT NULL
);

CREATE TABLE vratnice.opravneni_typ_pristupu (
    id_opravneni_typ_pristupu integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.opravneni_zavod (
    id_opravneni character varying(14) NOT NULL,
    id_zavod character varying(100) NOT NULL
);

CREATE TABLE vratnice.poschodi (
    id_poschodi character varying(14) NOT NULL,
    nazev character varying(80) NOT NULL,
    id_budova character varying(14) NOT NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.povoleni_vjezdu_vozidla (
    id_povoleni_vjezdu_vozidla character varying(14) NOT NULL,
    jmeno_zadatele character varying(30) NOT NULL,
    prijmeni_zadatele character varying(30) NOT NULL,
    spolecnost_zadatele character varying(120) NOT NULL,
    ico_zadatele character varying(50),
    duvod_zadosti character varying(255),
    id_ridic character varying(14),
    spolecnost_vozidla character varying(255),
    datum_od timestamp without time zone NOT NULL,
    datum_do timestamp without time zone NOT NULL,
    opakovany_vjezd boolean,
    stav character varying(50) DEFAULT 'vyžádáno'::character varying,
    id_stat integer
);

CREATE TABLE vratnice.povoleni_vjezdu_vozidla_rz_vozidla (
    id_povoleni_vjezdu_vozidla character varying(255) NOT NULL,
    rz_vozidla character varying(30) NOT NULL
);

CREATE TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla (
    id_povoleni_vjezdu_vozidla character varying(255) NOT NULL,
    id_vozidlo_typ integer
);

CREATE TABLE vratnice.povoleni_vjezdu_vozidla_zavod (
    id_povoleni_vjezdu_vozidla character varying(14) NOT NULL,
    id_zavod character varying(14) NOT NULL
);

CREATE TABLE vratnice.pracovni_pozice (
    id_pracovni_pozice character varying(14) NOT NULL,
    nazev character varying(1000) NOT NULL,
    zkratka character varying(100) NOT NULL,
    sap_id character varying(100) NOT NULL,
    sap_id_nadrizeny character varying(100),
    platnost_od date DEFAULT '1900-01-01'::date NOT NULL,
    platnost_do date DEFAULT '2999-12-31'::date NOT NULL,
    cas_aktualizace timestamp(6) without time zone,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    dohoda boolean DEFAULT false NOT NULL,
    sap_id_dohodar character varying(100)
);

CREATE TABLE vratnice.pracovni_pozice_log (
    id_pracovni_pozice_log character varying(14) NOT NULL,
    cas_volani timestamp(6) without time zone NOT NULL,
    cas_zpracovani timestamp(6) without time zone,
    chyba text,
    pocet_zaznamu integer DEFAULT 0 NOT NULL,
    ok boolean DEFAULT false NOT NULL,
    json_log text,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.pracovni_pozice_podrizene (
    id_pracovni_pozice_podrizene character varying(14) NOT NULL,
    id_pracovni_pozice character varying(14) NOT NULL,
    id_pracovni_pozice_podrizeny character varying(14) NOT NULL,
    primy_podrizeny boolean DEFAULT false NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.ridic (
    id_ridic character varying(14) NOT NULL,
    jmeno character varying(50) NOT NULL,
    prijmeni character varying(50) NOT NULL,
    cislo_op character varying(30) NOT NULL,
    firma character varying(120),
    datum_pouceni date
);

CREATE TABLE vratnice.role (
    authority character varying(100) NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE SEQUENCE vratnice.seq_budova_id_budova
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_externi_uzivatel_id_externi_uzivatel
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_historie_sluzebni_vozidlo_id_historie_sluzebni_vozidlo
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_historie_vypujcek_id_historie_vypujcek
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_historie_zaznamu_id_historie_zaznamu
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_jmeno_korektura_id_jmeno_korektura
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_klic_id_klic
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_kmenova_data_id_kmenova_data
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_lokalita_id_lokalita
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_najemnik_navstevnicka_karta_id_najemnik_navstevnicka_karta
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_navsteva_osoba_id_navsteva_osoba
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_navstevni_listek_id_navstevni_listek
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_opravneni_id_opravneni
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_poschodi_id_poschodi
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_povoleni_vjezdu_vozidla_id_povoleni_vjezdu_vozidla
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_pracovni_pozice_id_pracovni_pozice
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_pracovni_pozice_log_id_pracovni_pozice_log
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_pracovni_pozice_podrizene_id_pracovni_pozice_podrizene
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_ridic_id_ridic
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_sluzebni_vozidlo_id_sluzebni_vozidlo
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_uzivatel_id_uzivatel
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_uzivatelske_nastaveni_id_uzivatelske_nastaveni
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_vjezd_vozidla_id_vjezd_vozidla
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_vyjezd_vozidla_id_vyjezd_vozidla
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_zadost_klic_id_zadost_klic
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_zakazka_id_zakazka
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_zavod_id_zavod
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE vratnice.seq_zdrojovy_text_id_zdrojovy_text
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE vratnice.sluzebni_vozidlo (
    id_sluzebni_vozidlo character varying(14) NOT NULL,
    id_zavod character varying(14),
    datum_od date,
    aktivita boolean DEFAULT true NOT NULL,
    rz character varying(30),
    poznamka character varying(4000),
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100),
    id_vozidlo_typ integer,
    id_sluzebni_vozidlo_kategorie integer,
    id_sluzebni_vozidlo_funkce integer,
    id_sluzebni_vozidlo_stav integer
);

CREATE TABLE vratnice.sluzebni_vozidlo_funkce (
    id_sluzebni_vozidlo_funkce integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.sluzebni_vozidlo_kategorie (
    id_sluzebni_vozidlo_kategorie integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.sluzebni_vozidlo_lokalita (
    id_sluzebni_vozidlo character varying(14) NOT NULL,
    id_lokalita character varying(14) NOT NULL
);

CREATE TABLE vratnice.sluzebni_vozidlo_stav (
    id_sluzebni_vozidlo_stav integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.stat (
    id_stat integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.tmp_opravneni_vse (
    id_tmp_opravneni_vse character varying(200) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    authority character varying(100) NOT NULL
);

CREATE TABLE vratnice.tmp_opravneni_vyber (
    id_tmp_opravneni_vyber character varying(200) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    id_uzivatel_podrizeny character varying(14) NOT NULL,
    authority character varying(100) NOT NULL
);

CREATE TABLE vratnice.tmp_stav_operace (
    id_uzivatel character varying(14) NOT NULL,
    id_operace character varying(1000) NOT NULL,
    title character varying(100) NOT NULL,
    message text,
    status character varying(100) NOT NULL,
    size integer,
    actual integer,
    progress integer,
    start timestamp(6) without time zone NOT NULL,
    cas timestamp(6) without time zone NOT NULL,
    trvani integer
);

CREATE TABLE vratnice.uzivatel (
    id_uzivatel character varying(14) NOT NULL,
    id_zavod character varying(14) NOT NULL,
    id_kmenova_data character varying(14),
    sap_id character varying(100) NOT NULL,
    id_pracovni_pozice character varying(14),
    jmeno character varying(100),
    prijmeni character varying(100),
    titul_pred character varying(100),
    titul_za character varying(100),
    email character varying(1000),
    soukromy_email character varying(1000),
    tel character varying(1000),
    nazev character varying(1000),
    datum_od date NOT NULL,
    datum_do date,
    platnost_ke_dni timestamp(6) without time zone,
    cas_aktualizace timestamp(6) without time zone,
    ukonceno boolean DEFAULT false NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    id_zavod_vyber character varying(14),
    id_zastup character varying(14),
    ulice character varying(400),
    psc character varying(400),
    obec character varying(400),
    cislo_popisne character varying(400),
    dilci_personalni_oblast character varying(100),
    id_zakazka character varying(14),
    pruzna_prac_doba boolean DEFAULT false NOT NULL,
    cip_1 character varying(100),
    cip_2 character varying(100)
);

CREATE TABLE vratnice.uzivatel_modul (
    id_uzivatel character varying(14) NOT NULL,
    modul character varying(4000) NOT NULL
);

CREATE TABLE vratnice.uzivatel_navstevni_listek_typ (
    id_uzivatel character varying(14) NOT NULL,
    id_navstevni_listek_typ integer NOT NULL
);

CREATE TABLE vratnice.uzivatel_opravneni (
    id_uzivatel character varying(14) NOT NULL,
    id_opravneni character varying(14) NOT NULL
);

CREATE TABLE vratnice.uzivatel_zavod (
    id_uzivatel character varying(14) NOT NULL,
    id_zavod character varying(14) NOT NULL
);

CREATE TABLE vratnice.uzivatelske_nastaveni (
    id_uzivatelske_nastaveni character varying(14) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    klic character varying(100),
    hodnota text
);

CREATE TABLE vratnice.vjezd_vozidla (
    id_vjezd_vozidla character varying(14) NOT NULL,
    id_ridic character varying(14),
    rz_vozidla character varying(30) NOT NULL,
    opakovany_vjezd integer,
    cas_prijezdu timestamp with time zone NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true,
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100),
    id_vozidlo_typ integer
);

CREATE TABLE vratnice.vozidlo_typ (
    id_vozidlo_typ integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

CREATE TABLE vratnice.vyjezd_vozidla (
    id_vyjezd_vozidla character varying(14) NOT NULL,
    rz_vozidla character varying(30) NOT NULL,
    naklad boolean DEFAULT false NOT NULL,
    cislo_pruchodky character varying(30),
    opakovany_vjezd boolean,
    cas_odjezdu timestamp with time zone NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true,
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.zadost_externi (
    id_zadost_externi character varying(14) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    cas timestamp(6) without time zone NOT NULL,
    typ character varying(4000) NOT NULL,
    id_uzivatel_vytvoril character varying(14),
    datum_predani timestamp(6) without time zone,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.zadost_externi_zaznam (
    id_zadost_externi character varying(14) NOT NULL,
    id_zaznam character varying(14) NOT NULL
);

CREATE TABLE vratnice.zadost_klic (
    id_zadost_klic character varying(14) NOT NULL,
    id_klic character varying(14) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    stav character varying(30) NOT NULL,
    trvala boolean DEFAULT true,
    datum_od date,
    datum_do date,
    duvod text,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true,
    cas_zmn timestamp(6) without time zone,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.zakazka (
    id_zakazka character varying(14) NOT NULL,
    id_zavod character varying(14) NOT NULL,
    nazev character varying(1000) NOT NULL,
    sap_id character varying(100) NOT NULL,
    platnost_od date NOT NULL,
    platnost_do date NOT NULL,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100),
    virtualni boolean DEFAULT false NOT NULL
);

CREATE TABLE vratnice.zastup (
    guid character varying(100) NOT NULL,
    id_uzivatel character varying(14) NOT NULL,
    id_uzivatel_zastupce character varying(14) NOT NULL,
    platnost_od timestamp(6) without time zone NOT NULL,
    platnost_do timestamp(6) without time zone NOT NULL,
    distribuovano boolean DEFAULT false NOT NULL,
    chyba_distribuce text,
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.zavod (
    id_zavod character varying(14) NOT NULL,
    nazev character varying(1000) NOT NULL,
    sap_id character varying(100) NOT NULL,
    barva_pozadi character varying(10),
    barva_pisma character varying(10),
    poznamka character varying(4000),
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) without time zone NOT NULL,
    zmenu_provedl character varying(100)
);

CREATE TABLE vratnice.zdrojovy_text (
    id_zdrojovy_text integer DEFAULT nextval(('vratnice.seq_zdrojovy_text_id_zdrojovy_text'::text)::regclass) NOT NULL,
    culture character varying(100) DEFAULT 'cs'::character varying NOT NULL,
    hash character varying(1000) NOT NULL,
    text text NOT NULL
);

INSERT INTO vratnice.externi_role VALUES ('ROLE_PERSONALISTIKA', 'EXT_ROLE_PERSONALISTIKA');

INSERT INTO vratnice.externi_uzivatel VALUES ('XXEU0000000001', 'ext1', 'ext1', '$2a$12$MHjASjXf51sJjKLkuOANt.t6Os8G163CQ40/A6DmUEuZRObM5ZPZ6', NULL, true, '2024-05-09 13:01:23.020108', NULL);

INSERT INTO vratnice.externi_uzivatel_role VALUES ('XXEU0000000001', 'ROLE_PERSONALISTIKA');

INSERT INTO vratnice.historie_sluzebni_vozidlo_akce VALUES (1, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO');
INSERT INTO vratnice.historie_sluzebni_vozidlo_akce VALUES (2, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO');
INSERT INTO vratnice.historie_sluzebni_vozidlo_akce VALUES (3, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO');
INSERT INTO vratnice.historie_sluzebni_vozidlo_akce VALUES (4, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO');
INSERT INTO vratnice.historie_sluzebni_vozidlo_akce VALUES (5, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO');

INSERT INTO vratnice.historie_vypujcek_akce VALUES (1, 'HISTORIE_VYPUJCEK_VYPUJCEN');
INSERT INTO vratnice.historie_vypujcek_akce VALUES (2, 'HISTORIE_VYPUJCEK_VRACEN');

INSERT INTO vratnice.historie_zaznamu_typ VALUES (0, 'TYP_HISTORIE_ZAZNAMU_NOVY');
INSERT INTO vratnice.historie_zaznamu_typ VALUES (1, 'TYP_HISTORIE_ZAZNAMU_ZMENA');
INSERT INTO vratnice.historie_zaznamu_typ VALUES (2, 'TYP_HISTORIE_ZAZNAMU_SMAZANI');

INSERT INTO vratnice.klic_typ VALUES (1, 'KLIC_PRIDELENY');
INSERT INTO vratnice.klic_typ VALUES (2, 'KLIC_ZASTUP');
INSERT INTO vratnice.klic_typ VALUES (3, 'KLIC_ZALOZNI');
INSERT INTO vratnice.klic_typ VALUES (4, 'KLIC_OSTRAHA');
INSERT INTO vratnice.klic_typ VALUES (5, 'KLIC_TREZOR');
INSERT INTO vratnice.klic_typ VALUES (6, 'KLIC_UKLID');

INSERT INTO vratnice.navstevni_listek_typ VALUES (1, 'NAVSTEVNI_LISTEK_ELEKTRONICKY');
INSERT INTO vratnice.navstevni_listek_typ VALUES (2, 'NAVSTEVNI_LISTEK_PAPIROVY');

INSERT INTO vratnice.opravneni VALUES ('XXOP0000000001', 2, 'vratnice_hlavni_administrator', 'Hlavní administrátor', NULL, true, '2024-06-11 08:52:27.876', 'TKUZ0000000004', true);

INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_ZAMESTNANEC');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_OPRAVNENI');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_EXTERNICH_UZIVATELU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_ZAVODU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_CISELNIKY_ZAKAZKY');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_UZIVATELU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_KC_UZIVATELE');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_ZASTUPU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_TESTER');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SERVIS_ORG_STR');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_RIDICU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_ZADATEL_KLICE');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_KLICU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_ZADOSTI_KLICU');
INSERT INTO vratnice.opravneni_role VALUES ('XXOP0000000001', 'ROLE_SPRAVA_VYPUJCEK_KLICU');

INSERT INTO vratnice.opravneni_typ_pristupu VALUES (1, 'TYP_PRIST_OPR_BEZ_PRISTUPU');
INSERT INTO vratnice.opravneni_typ_pristupu VALUES (2, 'TYP_PRIST_OPR_VSE');
INSERT INTO vratnice.opravneni_typ_pristupu VALUES (3, 'TYP_PRIST_OPR_VYBER');
INSERT INTO vratnice.opravneni_typ_pristupu VALUES (4, 'TYP_PRIST_OPR_PRIMI_PODRIZENI');
INSERT INTO vratnice.opravneni_typ_pristupu VALUES (5, 'TYP_PRIST_OPR_VSICHNI_PODRIZENI');

INSERT INTO vratnice.opravneni_zavod VALUES ('XXOP0000000001', 'XXZA0000000001');
INSERT INTO vratnice.opravneni_zavod VALUES ('XXOP0000000001', 'XXZA0000000006');
INSERT INTO vratnice.opravneni_zavod VALUES ('XXOP0000000001', 'XXZA0000000007');
INSERT INTO vratnice.opravneni_zavod VALUES ('XXOP0000000001', 'XXZA0000000008');

INSERT INTO vratnice.role VALUES ('ROLE_ZAMESTNANEC', 'ROLE_ZAMESTNANEC');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_OPRAVNENI', 'ROLE_SPRAVA_OPRAVNENI');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_EXTERNICH_UZIVATELU', 'ROLE_SPRAVA_EXTERNICH_UZIVATELU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_ZAVODU', 'ROLE_SPRAVA_ZAVODU');
INSERT INTO vratnice.role VALUES ('ROLE_CISELNIKY_ZAKAZKY', 'ROLE_CISELNIKY_ZAKAZKY');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_UZIVATELU', 'ROLE_SPRAVA_UZIVATELU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_RIDICU', 'ROLE_SPRAVA_RIDICU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_KC_UZIVATELE', 'ROLE_SPRAVA_KC_UZIVATELE');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_ZASTUPU', 'ROLE_SPRAVA_ZASTUPU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE', 'ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE');
INSERT INTO vratnice.role VALUES ('ROLE_TESTER', 'ROLE_TESTER');
INSERT INTO vratnice.role VALUES ('ROLE_SERVIS_ORG_STR', 'ROLE_SERVIS_ORG_STR');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_KLICU', 'ROLE_SPRAVA_KLICU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_ZADOSTI_KLICU', 'ROLE_SPRAVA_ZADOSTI_KLICU');
INSERT INTO vratnice.role VALUES ('ROLE_SPRAVA_VYPUJCEK_KLICU', 'ROLE_SPRAVA_VYPUJCEK_KLICU');
INSERT INTO vratnice.role VALUES ('ROLE_ZADATEL_KLICE', 'ROLE_ZADATEL_KLICE');

INSERT INTO vratnice.sluzebni_vozidlo_funkce VALUES (1, 'SLUZEBNI_VOZIDLO_FUNKCE_REDITEL');
INSERT INTO vratnice.sluzebni_vozidlo_funkce VALUES (2, 'SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK');

INSERT INTO vratnice.sluzebni_vozidlo_kategorie VALUES (1, 'SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE');
INSERT INTO vratnice.sluzebni_vozidlo_kategorie VALUES (2, 'SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE');
INSERT INTO vratnice.sluzebni_vozidlo_kategorie VALUES (3, 'SLUZEBNI_VOZIDLO_KATEGORIE_JINE');

INSERT INTO vratnice.sluzebni_vozidlo_stav VALUES (1, 'SLUZEBNI_VOZIDLO_STAV_AKTIVNI');
INSERT INTO vratnice.sluzebni_vozidlo_stav VALUES (2, 'SLUZEBNI_VOZIDLO_STAV_BLOKOVANE');

INSERT INTO vratnice.stat VALUES (1, 'STAT_CESKA_REPUBLIKA');
INSERT INTO vratnice.stat VALUES (2, 'STAT_SLOVENSKO');
INSERT INTO vratnice.stat VALUES (3, 'STAT_POLSKO');
INSERT INTO vratnice.stat VALUES (4, 'STAT_NEMECKO');
INSERT INTO vratnice.stat VALUES (5, 'STAT_RAKOUSKO');

INSERT INTO vratnice.vozidlo_typ VALUES (1, 'VOZIDLO_OSOBNI');
INSERT INTO vratnice.vozidlo_typ VALUES (2, 'VOZIDLO_DODAVKA');
INSERT INTO vratnice.vozidlo_typ VALUES (3, 'VOZIDLO_NAKLADNI');
INSERT INTO vratnice.vozidlo_typ VALUES (4, 'VOZIDLO_SPECIALNI');
INSERT INTO vratnice.vozidlo_typ VALUES (5, 'VOZIDLO_IZS');

INSERT INTO vratnice.zavod VALUES ('XXZA0000000001', 'ŘSP', 'D100', '#8bb875', '#ffffff', NULL, true, '2024-05-09 13:01:23.020108', NULL);
INSERT INTO vratnice.zavod VALUES ('XXZA0000000006', 'HBZS', 'D600', '#f9c5af', '#ffffff', NULL, true, '2024-05-09 13:01:23.020108', NULL);
INSERT INTO vratnice.zavod VALUES ('XXZA0000000008', 'PKÚ', 'D800', '#7f9bcb', '#ffffff', NULL, true, '2024-05-09 13:01:23.020108', NULL);
INSERT INTO vratnice.zavod VALUES ('XXZA0000000007', 'Darkov', 'D700', '#333831', '#ffffff', NULL, true, '2024-07-04 10:50:00.377', 'TKUZ0000000004');

INSERT INTO vratnice.zdrojovy_text VALUES (1, 'cs', 'ROLE_ZAMESTNANEC', 'Zaměstnanec');
INSERT INTO vratnice.zdrojovy_text VALUES (2, 'cs', 'ROLE_SPRAVA_OPRAVNENI', 'Správa oprávnění');
INSERT INTO vratnice.zdrojovy_text VALUES (3, 'cs', 'ROLE_SPRAVA_EXTERNICH_UZIVATELU', 'Správa externích uživatelů');
INSERT INTO vratnice.zdrojovy_text VALUES (4, 'cs', 'ROLE_SPRAVA_ZAVODU', 'Správa závodu');
INSERT INTO vratnice.zdrojovy_text VALUES (5, 'cs', 'TYP_HISTORIE_ZAZNAMU_NOVY', 'Nový');
INSERT INTO vratnice.zdrojovy_text VALUES (6, 'cs', 'TYP_HISTORIE_ZAZNAMU_ZMENA', 'Změna');
INSERT INTO vratnice.zdrojovy_text VALUES (7, 'cs', 'TYP_HISTORIE_ZAZNAMU_SMAZANI', 'Smazání');
INSERT INTO vratnice.zdrojovy_text VALUES (8, 'cs', 'TYP_PRIST_OPR_BEZ_PRISTUPU', 'Bez přístupu');
INSERT INTO vratnice.zdrojovy_text VALUES (9, 'cs', 'TYP_PRIST_OPR_VSE', 'Vše');
INSERT INTO vratnice.zdrojovy_text VALUES (10, 'cs', 'TYP_PRIST_OPR_VYBER', 'Výběrem');
INSERT INTO vratnice.zdrojovy_text VALUES (11, 'cs', 'TYP_PRIST_OPR_PRIMI_PODRIZENI', 'Přímí podřízení');
INSERT INTO vratnice.zdrojovy_text VALUES (12, 'cs', 'TYP_PRIST_OPR_VSICHNI_PODRIZENI', 'Všichni podřízení');
INSERT INTO vratnice.zdrojovy_text VALUES (13, 'cs', 'EXT_ROLE_PERSONALISTIKA', 'Personalistika');
INSERT INTO vratnice.zdrojovy_text VALUES (14, 'cs', 'COLUMN_NAME_NAZEV', 'Název');
INSERT INTO vratnice.zdrojovy_text VALUES (15, 'cs', 'COLUMN_NAME_JMENO', 'Jméno');
INSERT INTO vratnice.zdrojovy_text VALUES (16, 'cs', 'COLUMN_NAME_PRIJMENI', 'Příjmení');
INSERT INTO vratnice.zdrojovy_text VALUES (17, 'cs', 'COLUMN_NAME_TITUL_PRED', 'Titul před');
INSERT INTO vratnice.zdrojovy_text VALUES (18, 'cs', 'COLUMN_NAME_TITUL_ZA', 'Titul za');
INSERT INTO vratnice.zdrojovy_text VALUES (19, 'cs', 'COLUMN_NAME_EMAIL', 'E-mail');
INSERT INTO vratnice.zdrojovy_text VALUES (20, 'cs', 'COLUMN_NAME_SOUKROMY_EMAIL', 'Soukromý e-mail');
INSERT INTO vratnice.zdrojovy_text VALUES (21, 'cs', 'COLUMN_NAME_POZNAMKA', 'Poznámka');
INSERT INTO vratnice.zdrojovy_text VALUES (22, 'cs', 'COLUMN_NAME_AKTIVITA', 'Aktivita');
INSERT INTO vratnice.zdrojovy_text VALUES (23, 'cs', 'COLUMN_NAME_ID_KMENOVA_DATA', 'Kmenová data');
INSERT INTO vratnice.zdrojovy_text VALUES (24, 'cs', 'COLUMN_NAME_PLATNOST_KE_DNI', 'Platnost ke dni');
INSERT INTO vratnice.zdrojovy_text VALUES (25, 'cs', 'COLUMN_NAME_ID_OPRAVNENI', 'Oprávnění');
INSERT INTO vratnice.zdrojovy_text VALUES (26, 'cs', 'COLUMN_NAME_ID_OPRAVNENI_TYP_PRISTUPU', 'Omezení přístupu oprávnění k zaměstnancům');
INSERT INTO vratnice.zdrojovy_text VALUES (27, 'cs', 'COLUMN_NAME_KOD', 'Kód');
INSERT INTO vratnice.zdrojovy_text VALUES (28, 'cs', 'COLUMN_NAME_AUTHORITY', 'Role');
INSERT INTO vratnice.zdrojovy_text VALUES (29, 'cs', 'COLUMN_NAME_ID_ZAVOD', 'Závod');
INSERT INTO vratnice.zdrojovy_text VALUES (30, 'cs', 'COLUMN_NAME_ID_PRACOVNI_POZICE', 'Praacovní pozice');
INSERT INTO vratnice.zdrojovy_text VALUES (31, 'cs', 'ROLE_CISELNIKY_ZAKAZKY', 'Správce zakázek');
INSERT INTO vratnice.zdrojovy_text VALUES (32, 'cs', 'ROLE_SPRAVA_UZIVATELU', 'Správce uživatelů');
INSERT INTO vratnice.zdrojovy_text VALUES (33, 'cs', 'ROLE_SPRAVA_RIDICU', 'Správce řidičů');
INSERT INTO vratnice.zdrojovy_text VALUES (34, 'cs', 'ROLE_SPRAVA_KC_UZIVATELE', 'Správce - účet KeyCloak - uživatelé');
INSERT INTO vratnice.zdrojovy_text VALUES (35, 'cs', 'ROLE_SPRAVA_ZASTUPU', 'Správce - zástupy');
INSERT INTO vratnice.zdrojovy_text VALUES (36, 'cs', 'ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE', 'Správce - zástupy - synchronizace');
INSERT INTO vratnice.zdrojovy_text VALUES (37, 'cs', 'COLUMN_NAME_ZASTUP', 'Zástup');
INSERT INTO vratnice.zdrojovy_text VALUES (38, 'cs', 'COLUMN_NAME_ID_ZASTUP', 'Zástup');
INSERT INTO vratnice.zdrojovy_text VALUES (39, 'cs', 'COLUMN_NAME_ULICE', 'Ulice');
INSERT INTO vratnice.zdrojovy_text VALUES (40, 'cs', 'COLUMN_NAME_PSC', 'PSČ');
INSERT INTO vratnice.zdrojovy_text VALUES (41, 'cs', 'COLUMN_NAME_OBEC', 'Obec');
INSERT INTO vratnice.zdrojovy_text VALUES (42, 'cs', 'COLUMN_NAME_CISLO_POPISNE', 'Číslo popisné');
INSERT INTO vratnice.zdrojovy_text VALUES (43, 'cs', 'COLUMN_NAME_DILCI_PERSONALNI_OBLAST', 'Dílčí personální oblast');
INSERT INTO vratnice.zdrojovy_text VALUES (44, 'cs', 'ROLE_TESTER', 'Tester - přístup k nově vytvořeným funkcím aplikace');
INSERT INTO vratnice.zdrojovy_text VALUES (45, 'cs', 'COLUMN_NAME_ZAKAZKA', 'Zakázka');
INSERT INTO vratnice.zdrojovy_text VALUES (46, 'cs', 'COLUMN_PRUZNA_PRAC_DOBA', 'Pružná pracovní doba');
INSERT INTO vratnice.zdrojovy_text VALUES (47, 'cs', 'ROLE_SERVIS_ORG_STR', 'Servis - organizační struktura');
INSERT INTO vratnice.zdrojovy_text VALUES (48, 'cs', 'ROLE_SPRAVA_KLICU', 'Správa klíčů');
INSERT INTO vratnice.zdrojovy_text VALUES (49, 'cs', 'ROLE_SPRAVA_ZADOSTI_KLICU', 'Správa žádostí klíčů');
INSERT INTO vratnice.zdrojovy_text VALUES (50, 'cs', 'ROLE_SPRAVA_VYPUJCEK_KLICU', 'Správa výpůjček klíčů');
INSERT INTO vratnice.zdrojovy_text VALUES (51, 'cs', 'ROLE_ZADATEL_KLICE', 'Žadatel o klíč');
INSERT INTO vratnice.zdrojovy_text VALUES (56, 'cs', 'NAVSTEVNI_LISTEK_ELEKTRONICKY', 'Elektronický');
INSERT INTO vratnice.zdrojovy_text VALUES (57, 'cs', 'NAVSTEVNI_LISTEK_PAPIROVY', 'Papírový');
INSERT INTO vratnice.zdrojovy_text VALUES (58, 'cs', 'KLIC_PRIDELENY', 'přidělený');
INSERT INTO vratnice.zdrojovy_text VALUES (59, 'cs', 'KLIC_ZASTUP', 'zástup');
INSERT INTO vratnice.zdrojovy_text VALUES (60, 'cs', 'KLIC_ZALOZNI', 'záložní');
INSERT INTO vratnice.zdrojovy_text VALUES (61, 'cs', 'KLIC_OSTRAHA', 'ostraha');
INSERT INTO vratnice.zdrojovy_text VALUES (62, 'cs', 'KLIC_TREZOR', 'trezor');
INSERT INTO vratnice.zdrojovy_text VALUES (63, 'cs', 'KLIC_UKLID', 'úklid');
INSERT INTO vratnice.zdrojovy_text VALUES (69, 'cs', 'VOZIDLO_OSOBNI', 'osobní');
INSERT INTO vratnice.zdrojovy_text VALUES (70, 'cs', 'VOZIDLO_DODAVKA', 'dodávka');
INSERT INTO vratnice.zdrojovy_text VALUES (71, 'cs', 'VOZIDLO_NAKLADNI', 'nákladní');
INSERT INTO vratnice.zdrojovy_text VALUES (72, 'cs', 'VOZIDLO_SPECIALNI', 'speciální');
INSERT INTO vratnice.zdrojovy_text VALUES (73, 'cs', 'VOZIDLO_IZS', 'IZS');
INSERT INTO vratnice.zdrojovy_text VALUES (74, 'cs', 'SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE', 'referentské');
INSERT INTO vratnice.zdrojovy_text VALUES (75, 'cs', 'SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE', 'manažerské');
INSERT INTO vratnice.zdrojovy_text VALUES (76, 'cs', 'SLUZEBNI_VOZIDLO_KATEGORIE_JINE', 'jiné');
INSERT INTO vratnice.zdrojovy_text VALUES (77, 'cs', 'SLUZEBNI_VOZIDLO_FUNKCE_REDITEL', 'ředitel');
INSERT INTO vratnice.zdrojovy_text VALUES (78, 'cs', 'SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK', 'náměstek');
INSERT INTO vratnice.zdrojovy_text VALUES (81, 'cs', 'SLUZEBNI_VOZIDLO_STAV_AKTIVNI', 'aktivní');
INSERT INTO vratnice.zdrojovy_text VALUES (82, 'cs', 'SLUZEBNI_VOZIDLO_STAV_BLOKOVANE', 'blokované');
INSERT INTO vratnice.zdrojovy_text VALUES (83, 'cs', 'STAT_CESKA_REPUBLIKA', 'Česká republika');
INSERT INTO vratnice.zdrojovy_text VALUES (84, 'cs', 'STAT_SLOVENSKO', 'Slovensko');
INSERT INTO vratnice.zdrojovy_text VALUES (85, 'cs', 'STAT_POLSKO', 'Polsko');
INSERT INTO vratnice.zdrojovy_text VALUES (86, 'cs', 'STAT_NEMECKO', 'Německo');
INSERT INTO vratnice.zdrojovy_text VALUES (87, 'cs', 'STAT_RAKOUSKO', 'Rakousko');
INSERT INTO vratnice.zdrojovy_text VALUES (103, 'cs', 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO', 'vytvořeno');
INSERT INTO vratnice.zdrojovy_text VALUES (104, 'cs', 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO', 'upraveno');
INSERT INTO vratnice.zdrojovy_text VALUES (105, 'cs', 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO', 'odstraněno');
INSERT INTO vratnice.zdrojovy_text VALUES (106, 'cs', 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO', 'blokovano');
INSERT INTO vratnice.zdrojovy_text VALUES (107, 'cs', 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO', 'obnoveno');
INSERT INTO vratnice.zdrojovy_text VALUES (108, 'cs', 'HISTORIE_VYPUJCEK_VYPUJCEN', 'vypůjčen');
INSERT INTO vratnice.zdrojovy_text VALUES (109, 'cs', 'HISTORIE_VYPUJCEK_VRACEN', 'vrácen');
INSERT INTO vratnice.zdrojovy_text VALUES (110, 'cs', 'COLUMN_CIP_1', 'Přístupvý čip');
INSERT INTO vratnice.zdrojovy_text VALUES (111, 'cs', 'COLUMN_CIP_2', 'Přístupvý čip - sekundární');


SELECT pg_catalog.setval('vratnice.seq_externi_uzivatel_id_externi_uzivatel', 1, false);

SELECT pg_catalog.setval('vratnice.seq_opravneni_id_opravneni', 1, false);

SELECT pg_catalog.setval('vratnice.seq_zavod_id_zavod', 1, false);

SELECT pg_catalog.setval('vratnice.seq_zdrojovy_text_id_zdrojovy_text', 111, true);

ALTER TABLE vratnice.najemnik_navstevnicka_karta
    ADD CONSTRAINT najemnik_navstevnicka_karta_cislo_op_key UNIQUE (cislo_op);

ALTER TABLE vratnice.budova
    ADD CONSTRAINT pk_budova PRIMARY KEY (id_budova);

ALTER TABLE vratnice.databaze
    ADD CONSTRAINT pk_databaze PRIMARY KEY (id_databaze);

ALTER TABLE vratnice.externi_role
    ADD CONSTRAINT pk_externi_role PRIMARY KEY (authority);

ALTER TABLE vratnice.externi_uzivatel
    ADD CONSTRAINT pk_externi_uzivatel PRIMARY KEY (id_externi_uzivatel);

ALTER TABLE vratnice.externi_uzivatel_role
    ADD CONSTRAINT pk_externi_uzivatel_role PRIMARY KEY (id_externi_uzivatel, authority);

ALTER TABLE vratnice.historie_sluzebni_vozidlo
    ADD CONSTRAINT pk_historie_sluzebni_vozidlo PRIMARY KEY (id_historie_sluzebni_vozidlo);

ALTER TABLE vratnice.historie_sluzebni_vozidlo_akce
    ADD CONSTRAINT pk_historie_sluzebni_vozidlo_akce PRIMARY KEY (id_historie_sluzebni_vozidlo_akce);

ALTER TABLE vratnice.historie_vypujcek
    ADD CONSTRAINT pk_historie_vypujcek PRIMARY KEY (id_historie_vypujcek);

ALTER TABLE vratnice.historie_vypujcek_akce
    ADD CONSTRAINT pk_historie_vypujcek_akce PRIMARY KEY (id_historie_vypujcek_akce);

ALTER TABLE vratnice.historie_zaznamu
    ADD CONSTRAINT pk_historie_zaznamu PRIMARY KEY (id_historie_zaznamu);

ALTER TABLE vratnice.historie_zaznamu_typ
    ADD CONSTRAINT pk_historie_zaznamu_typ PRIMARY KEY (id_historie_zaznamu_typ);

ALTER TABLE vratnice.jmeno_korektura
    ADD CONSTRAINT pk_jmeno_korektura PRIMARY KEY (id_jmeno_korektura);

ALTER TABLE vratnice.klic
    ADD CONSTRAINT pk_klic PRIMARY KEY (id_klic);

ALTER TABLE vratnice.klic_typ
    ADD CONSTRAINT pk_klic_typ PRIMARY KEY (id_klic_typ);

ALTER TABLE vratnice.kmenova_data
    ADD CONSTRAINT pk_kmenova_data PRIMARY KEY (id_kmenova_data);

ALTER TABLE vratnice.lokalita
    ADD CONSTRAINT pk_lokalita PRIMARY KEY (id_lokalita);

ALTER TABLE vratnice.najemnik_navstevnicka_karta
    ADD CONSTRAINT pk_najemnik_navstevnicka_karta PRIMARY KEY (id_najemnik_navstevnicka_karta);

ALTER TABLE vratnice.navsteva_osoba
    ADD CONSTRAINT pk_navsteva_osoba PRIMARY KEY (id_navsteva_osoba);

ALTER TABLE vratnice.navstevni_listek
    ADD CONSTRAINT pk_navstevni_listek PRIMARY KEY (id_navstevni_listek);

ALTER TABLE vratnice.navstevni_listek_navsteva_osoba
    ADD CONSTRAINT pk_navstevni_listek_navsteva_osoba PRIMARY KEY (id_navstevni_listek, id_navsteva_osoba);

ALTER TABLE vratnice.navstevni_listek_typ
    ADD CONSTRAINT pk_navstevni_listek_typ PRIMARY KEY (id_navstevni_listek_typ);

ALTER TABLE vratnice.navstevni_listek_uzivatel
    ADD CONSTRAINT pk_navstevni_listek_uzivatel PRIMARY KEY (id_navstevni_listek, id_uzivatel);

ALTER TABLE vratnice.opravneni
    ADD CONSTRAINT pk_opravneni PRIMARY KEY (id_opravneni);

ALTER TABLE vratnice.opravneni_pracovni_pozice
    ADD CONSTRAINT pk_opravneni_pracovni_pozice PRIMARY KEY (id_opravneni, id_pracovni_pozice);

ALTER TABLE vratnice.opravneni_role
    ADD CONSTRAINT pk_opravneni_role PRIMARY KEY (id_opravneni, authority);

ALTER TABLE vratnice.opravneni_typ_pristupu
    ADD CONSTRAINT pk_opravneni_typ_pristupu PRIMARY KEY (id_opravneni_typ_pristupu);

ALTER TABLE vratnice.opravneni_zavod
    ADD CONSTRAINT pk_opravneni_zavod PRIMARY KEY (id_opravneni, id_zavod);

ALTER TABLE vratnice.poschodi
    ADD CONSTRAINT pk_poschodi PRIMARY KEY (id_poschodi);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD CONSTRAINT pk_povoleni_vjezdu_vozidla PRIMARY KEY (id_povoleni_vjezdu_vozidla);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zavod
    ADD CONSTRAINT pk_povoleni_vjezdu_vozidla_zavod PRIMARY KEY (id_povoleni_vjezdu_vozidla, id_zavod);

ALTER TABLE vratnice.pracovni_pozice
    ADD CONSTRAINT pk_pracovni_pozice PRIMARY KEY (id_pracovni_pozice);

ALTER TABLE vratnice.pracovni_pozice_log
    ADD CONSTRAINT pk_pracovni_pozice_log PRIMARY KEY (id_pracovni_pozice_log);

ALTER TABLE vratnice.pracovni_pozice_podrizene
    ADD CONSTRAINT pk_pracovni_pozice_podrizene PRIMARY KEY (id_pracovni_pozice_podrizene);

ALTER TABLE vratnice.ridic
    ADD CONSTRAINT pk_ridic PRIMARY KEY (id_ridic);

ALTER TABLE vratnice.role
    ADD CONSTRAINT pk_role PRIMARY KEY (authority);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT pk_sluzebni_vozidlo PRIMARY KEY (id_sluzebni_vozidlo);

ALTER TABLE vratnice.sluzebni_vozidlo_funkce
    ADD CONSTRAINT pk_sluzebni_vozidlo_funkce PRIMARY KEY (id_sluzebni_vozidlo_funkce);

ALTER TABLE vratnice.sluzebni_vozidlo_kategorie
    ADD CONSTRAINT pk_sluzebni_vozidlo_kategorie PRIMARY KEY (id_sluzebni_vozidlo_kategorie);

ALTER TABLE vratnice.sluzebni_vozidlo_lokalita
    ADD CONSTRAINT pk_sluzebni_vozidlo_lokalita PRIMARY KEY (id_sluzebni_vozidlo, id_lokalita);

ALTER TABLE vratnice.sluzebni_vozidlo_stav
    ADD CONSTRAINT pk_sluzebni_vozidlo_stav PRIMARY KEY (id_sluzebni_vozidlo_stav);

ALTER TABLE vratnice.stat
    ADD CONSTRAINT pk_stat PRIMARY KEY (id_stat);

ALTER TABLE vratnice.tmp_opravneni_vse
    ADD CONSTRAINT pk_tmp_opravneni_vse PRIMARY KEY (id_tmp_opravneni_vse);

ALTER TABLE vratnice.tmp_opravneni_vyber
    ADD CONSTRAINT pk_tmp_opravneni_vyber PRIMARY KEY (id_tmp_opravneni_vyber);

ALTER TABLE vratnice.tmp_stav_operace
    ADD CONSTRAINT pk_tmp_stav_operace PRIMARY KEY (id_uzivatel, id_operace);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT pk_uzivatel PRIMARY KEY (id_uzivatel);

ALTER TABLE vratnice.uzivatel_modul
    ADD CONSTRAINT pk_uzivatel_modul PRIMARY KEY (id_uzivatel, modul);

ALTER TABLE vratnice.uzivatel_navstevni_listek_typ
    ADD CONSTRAINT pk_uzivatel_navstevni_listek_typ PRIMARY KEY (id_uzivatel, id_navstevni_listek_typ);

ALTER TABLE vratnice.uzivatel_opravneni
    ADD CONSTRAINT pk_uzivatel_opravneni PRIMARY KEY (id_uzivatel, id_opravneni);

ALTER TABLE vratnice.uzivatel_zavod
    ADD CONSTRAINT pk_uzivatel_zavod PRIMARY KEY (id_uzivatel, id_zavod);

ALTER TABLE vratnice.uzivatelske_nastaveni
    ADD CONSTRAINT pk_uzivatelske_nastaveni PRIMARY KEY (id_uzivatelske_nastaveni);

ALTER TABLE vratnice.vjezd_vozidla
    ADD CONSTRAINT pk_vjezd_vozidla PRIMARY KEY (id_vjezd_vozidla);

ALTER TABLE vratnice.vozidlo_typ
    ADD CONSTRAINT pk_vozidlo_typ PRIMARY KEY (id_vozidlo_typ);

ALTER TABLE vratnice.vyjezd_vozidla
    ADD CONSTRAINT pk_vyjezd_vozidla PRIMARY KEY (id_vyjezd_vozidla);

ALTER TABLE vratnice.zadost_externi
    ADD CONSTRAINT pk_zadost_externi PRIMARY KEY (id_zadost_externi);

ALTER TABLE vratnice.zadost_externi_zaznam
    ADD CONSTRAINT pk_zadost_externi_zaznam PRIMARY KEY (id_zadost_externi, id_zaznam);

ALTER TABLE vratnice.zadost_klic
    ADD CONSTRAINT pk_zadost_klic PRIMARY KEY (id_zadost_klic);

ALTER TABLE vratnice.zakazka
    ADD CONSTRAINT pk_zakazka PRIMARY KEY (id_zakazka);

ALTER TABLE vratnice.zastup
    ADD CONSTRAINT pk_zastup PRIMARY KEY (guid);

ALTER TABLE vratnice.zavod
    ADD CONSTRAINT pk_zavod PRIMARY KEY (id_zavod);

ALTER TABLE vratnice.zdrojovy_text
    ADD CONSTRAINT pk_zdrojovy_text PRIMARY KEY (id_zdrojovy_text);

ALTER TABLE vratnice.jmeno_korektura
    ADD CONSTRAINT uk_jmeno_vstup UNIQUE (jmeno_vstup);

ALTER TABLE vratnice.ridic
    ADD CONSTRAINT unique_cislo_op UNIQUE (cislo_op);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT unique_rz UNIQUE (rz);

ALTER TABLE vratnice.zdrojovy_text
    ADD CONSTRAINT zdrojovy_text_culture_hash_key UNIQUE (culture, hash);

CREATE INDEX ix_budova_id_lokalita ON vratnice.budova USING btree (id_lokalita);

CREATE UNIQUE INDEX ix_externi_uzivatel_username ON vratnice.externi_uzivatel USING btree (username);

CREATE INDEX ix_historie_sluzebni_vozidlo_id_sluzebni_vozidlo ON vratnice.historie_sluzebni_vozidlo USING btree (id_sluzebni_vozidlo);

CREATE INDEX ix_historie_sluzebni_vozidlo_id_uzivatel ON vratnice.historie_sluzebni_vozidlo USING btree (id_uzivatel);

CREATE INDEX ix_historie_vypujcek_id_zadost_klic ON vratnice.historie_vypujcek USING btree (id_zadost_klic);

CREATE INDEX ix_lokalita_id_zavod ON vratnice.lokalita USING btree (id_zavod);

CREATE INDEX ix_navstevni_listek_navsteva_osoba_id_navsteva_osoba ON vratnice.navstevni_listek_navsteva_osoba USING btree (id_navsteva_osoba);

CREATE INDEX ix_navstevni_listek_navsteva_osoba_id_navstevni_listek ON vratnice.navstevni_listek_navsteva_osoba USING btree (id_navstevni_listek);

CREATE INDEX ix_navstevni_listek_uzivatel_id_navstevni_listek ON vratnice.navstevni_listek_uzivatel USING btree (id_navstevni_listek);

CREATE INDEX ix_navstevni_listek_uzivatel_id_uzivatel ON vratnice.navstevni_listek_uzivatel USING btree (id_uzivatel);

CREATE INDEX ix_poschodi_id_budova ON vratnice.poschodi USING btree (id_budova);

CREATE INDEX ix_povoleni_vjezdu_vozidla_id_ridic ON vratnice.povoleni_vjezdu_vozidla USING btree (id_ridic);

CREATE INDEX ix_povoleni_vjezdu_vozidla_rz_vozidla_id_povoleni_vjezdu_vozidl ON vratnice.povoleni_vjezdu_vozidla_rz_vozidla USING btree (id_povoleni_vjezdu_vozidla);

CREATE INDEX ix_povoleni_vjezdu_vozidla_typ_vozidla_id_povoleni_vjezdu_vozid ON vratnice.povoleni_vjezdu_vozidla_typ_vozidla USING btree (id_povoleni_vjezdu_vozidla);

CREATE INDEX ix_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_zavod USING btree (id_povoleni_vjezdu_vozidla);

CREATE INDEX ix_povoleni_vjezdu_vozidla_zavod_id_zavod ON vratnice.povoleni_vjezdu_vozidla_zavod USING btree (id_zavod);

CREATE UNIQUE INDEX ix_pracovni_pozice_sap_id ON vratnice.pracovni_pozice USING btree (sap_id);

CREATE INDEX ix_sluzebni_vozidlo_id_sluzebni_vozidlo ON vratnice.zavod USING btree (id_zavod);

CREATE INDEX ix_sluzebni_vozidlo_id_zavod ON vratnice.sluzebni_vozidlo USING btree (id_zavod);

CREATE INDEX ix_sluzebni_vozidlo_lokalita_id_lokalita ON vratnice.sluzebni_vozidlo_lokalita USING btree (id_lokalita);

CREATE INDEX ix_sluzebni_vozidlo_lokalita_id_sluzebni_vozidlo ON vratnice.sluzebni_vozidlo_lokalita USING btree (id_sluzebni_vozidlo);

CREATE UNIQUE INDEX ix_uzivatel_sap_id ON vratnice.uzivatel USING btree (sap_id);

CREATE UNIQUE INDEX ix_uzivatelske_nastaveni_id_uzivatel_klic ON vratnice.uzivatelske_nastaveni USING btree (id_uzivatel, klic);

CREATE INDEX ix_vjezd_vozidla_id_ridic ON vratnice.vjezd_vozidla USING btree (id_ridic);

CREATE INDEX ix_zadost_klic_id_klic ON vratnice.zadost_klic USING btree (id_klic);

CREATE INDEX ix_zadost_klic_id_uzivatel ON vratnice.zadost_klic USING btree (id_uzivatel);

CREATE UNIQUE INDEX ix_zakazka_id_zavod_sap_id ON vratnice.zakazka USING btree (id_zavod, sap_id);

CREATE UNIQUE INDEX ix_zavod_sap_id ON vratnice.zavod USING btree (sap_id);

CREATE UNIQUE INDEX ix_zdrojovy_text_culture_hash ON vratnice.zdrojovy_text USING btree (culture, hash);

CREATE TRIGGER opravneni_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.opravneni FOR EACH ROW EXECUTE FUNCTION vratnice.opravneni_log();

CREATE TRIGGER opravneni_pracovni_pozice_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.opravneni_pracovni_pozice FOR EACH ROW EXECUTE FUNCTION vratnice.opravneni_pracovni_pozice_log();

CREATE TRIGGER opravneni_role_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.opravneni_role FOR EACH ROW EXECUTE FUNCTION vratnice.opravneni_role_log();

CREATE TRIGGER opravneni_zavod_log_trg BEFORE INSERT OR DELETE ON vratnice.opravneni_zavod FOR EACH ROW EXECUTE FUNCTION vratnice.opravneni_zavod_log();

CREATE TRIGGER uzivatel_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.uzivatel FOR EACH ROW EXECUTE FUNCTION vratnice.uzivatel_log();

CREATE TRIGGER uzivatel_opravneni_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.uzivatel_opravneni FOR EACH ROW EXECUTE FUNCTION vratnice.uzivatel_opravneni_log();

CREATE TRIGGER uzivatel_zavod_log_trg AFTER INSERT OR DELETE OR UPDATE ON vratnice.uzivatel_zavod FOR EACH ROW EXECUTE FUNCTION vratnice.uzivatel_zavod_log();

ALTER TABLE vratnice.budova
    ADD CONSTRAINT fk_budova_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita(id_lokalita);

ALTER TABLE vratnice.externi_uzivatel_role
    ADD CONSTRAINT fk_externi_uzivatel_role_authority FOREIGN KEY (authority) REFERENCES vratnice.externi_role(authority);

ALTER TABLE vratnice.externi_uzivatel_role
    ADD CONSTRAINT fk_externi_uzivatel_role_id_externi_uzivatel FOREIGN KEY (id_externi_uzivatel) REFERENCES vratnice.externi_uzivatel(id_externi_uzivatel);

ALTER TABLE vratnice.historie_sluzebni_vozidlo
    ADD CONSTRAINT fk_historie_sluzebni_vozidlo_id_historie_sluzebni_vozidlo_akce FOREIGN KEY (id_historie_sluzebni_vozidlo_akce) REFERENCES vratnice.historie_sluzebni_vozidlo_akce(id_historie_sluzebni_vozidlo_akce);

ALTER TABLE vratnice.historie_sluzebni_vozidlo
    ADD CONSTRAINT fk_historie_sluzebni_vozidlo_id_sluzebni_vozidlo FOREIGN KEY (id_sluzebni_vozidlo) REFERENCES vratnice.sluzebni_vozidlo(id_sluzebni_vozidlo);

ALTER TABLE vratnice.historie_sluzebni_vozidlo
    ADD CONSTRAINT fk_historie_sluzebni_vozidlo_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.historie_vypujcek
    ADD CONSTRAINT fk_historie_vypujcek_id_historie_vypujcek_akce FOREIGN KEY (id_historie_vypujcek_akce) REFERENCES vratnice.historie_vypujcek_akce(id_historie_vypujcek_akce);

ALTER TABLE vratnice.historie_vypujcek
    ADD CONSTRAINT fk_historie_vypujcek_id_vratny FOREIGN KEY (id_vratny) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.historie_vypujcek
    ADD CONSTRAINT fk_historie_vypujcek_id_zadost_klic FOREIGN KEY (id_zadost_klic) REFERENCES vratnice.zadost_klic(id_zadost_klic);

ALTER TABLE vratnice.historie_zaznamu
    ADD CONSTRAINT fk_historie_zaznamu_id_historie_zaznamu_typ FOREIGN KEY (id_historie_zaznamu_typ) REFERENCES vratnice.historie_zaznamu_typ(id_historie_zaznamu_typ);

ALTER TABLE vratnice.klic
    ADD CONSTRAINT fk_klic_id_budova FOREIGN KEY (id_budova) REFERENCES vratnice.budova(id_budova);

ALTER TABLE vratnice.klic
    ADD CONSTRAINT fk_klic_id_klic_typ FOREIGN KEY (id_klic_typ) REFERENCES vratnice.klic_typ(id_klic_typ);

ALTER TABLE vratnice.klic
    ADD CONSTRAINT fk_klic_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita(id_lokalita);

ALTER TABLE vratnice.klic
    ADD CONSTRAINT fk_klic_id_poschodi FOREIGN KEY (id_poschodi) REFERENCES vratnice.poschodi(id_poschodi);

ALTER TABLE vratnice.kmenova_data
    ADD CONSTRAINT fk_kmenova_data_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.lokalita
    ADD CONSTRAINT fk_lokalita_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.navstevni_listek
    ADD CONSTRAINT fk_navstevni_listek_id_navstevni_listek_typ FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ(id_navstevni_listek_typ);

ALTER TABLE vratnice.navstevni_listek_navsteva_osoba
    ADD CONSTRAINT fk_navstevni_listek_navsteva_osoba_id_navsteva_osoba FOREIGN KEY (id_navsteva_osoba) REFERENCES vratnice.navsteva_osoba(id_navsteva_osoba);

ALTER TABLE vratnice.navstevni_listek_navsteva_osoba
    ADD CONSTRAINT fk_navstevni_listek_navsteva_osoba_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek(id_navstevni_listek);

ALTER TABLE vratnice.navstevni_listek_uzivatel
    ADD CONSTRAINT fk_navstevni_listek_uzivatel_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek(id_navstevni_listek);

ALTER TABLE vratnice.navstevni_listek_uzivatel
    ADD CONSTRAINT fk_navstevni_listek_uzivatel_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.opravneni
    ADD CONSTRAINT fk_opravneni_id_opravneni_typ_pristupu FOREIGN KEY (id_opravneni_typ_pristupu) REFERENCES vratnice.opravneni_typ_pristupu(id_opravneni_typ_pristupu);

ALTER TABLE vratnice.opravneni_pracovni_pozice
    ADD CONSTRAINT fk_opravneni_pracovni_pozice_id_opravneni FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni(id_opravneni);

ALTER TABLE vratnice.opravneni_pracovni_pozice
    ADD CONSTRAINT fk_opravneni_pracovni_pozice_id_pracovni_pozice FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice(id_pracovni_pozice);

ALTER TABLE vratnice.opravneni_role
    ADD CONSTRAINT fk_opravneni_role_authority FOREIGN KEY (authority) REFERENCES vratnice.role(authority);

ALTER TABLE vratnice.opravneni_role
    ADD CONSTRAINT fk_opravneni_role_id_opravneni FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni(id_opravneni);

ALTER TABLE vratnice.opravneni_zavod
    ADD CONSTRAINT fk_opravneni_zavod_id_opravneni FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni(id_opravneni);

ALTER TABLE vratnice.opravneni_zavod
    ADD CONSTRAINT fk_opravneni_zavod_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.uzivatel_opravneni
    ADD CONSTRAINT fk_ouzivatel_opravneni_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.poschodi
    ADD CONSTRAINT fk_poschodi_id_budova FOREIGN KEY (id_budova) REFERENCES vratnice.budova(id_budova);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_ridic FOREIGN KEY (id_ridic) REFERENCES vratnice.ridic(id_ridic);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_stat FOREIGN KEY (id_stat) REFERENCES vratnice.stat(id_stat);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_rz_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_rz_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla(id_povoleni_vjezdu_vozidla) ON DELETE CASCADE;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_typ_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla(id_povoleni_vjezdu_vozidla) ON DELETE CASCADE;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_typ_vozidla_id_vozidlo_typ FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ(id_vozidlo_typ);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zavod
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla(id_povoleni_vjezdu_vozidla);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_zavod
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_zavod_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.pracovni_pozice_podrizene
    ADD CONSTRAINT fk_pracovni_pozice_podrizene_id_pracovni_pozice FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice(id_pracovni_pozice);

ALTER TABLE vratnice.pracovni_pozice_podrizene
    ADD CONSTRAINT fk_pracovni_pozice_podrizene_id_pracovni_pozice_podrizeny FOREIGN KEY (id_pracovni_pozice_podrizeny) REFERENCES vratnice.pracovni_pozice(id_pracovni_pozice);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_funkce FOREIGN KEY (id_sluzebni_vozidlo_funkce) REFERENCES vratnice.sluzebni_vozidlo_funkce(id_sluzebni_vozidlo_funkce);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_kategorie FOREIGN KEY (id_sluzebni_vozidlo_kategorie) REFERENCES vratnice.sluzebni_vozidlo_kategorie(id_sluzebni_vozidlo_kategorie);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_stav FOREIGN KEY (id_sluzebni_vozidlo_stav) REFERENCES vratnice.sluzebni_vozidlo_stav(id_sluzebni_vozidlo_stav);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT fk_sluzebni_vozidlo_id_vozidlo_typ FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ(id_vozidlo_typ);

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD CONSTRAINT fk_sluzebni_vozidlo_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.sluzebni_vozidlo_lokalita
    ADD CONSTRAINT fk_sluzebni_vozidlo_lokalita_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita(id_lokalita);

ALTER TABLE vratnice.sluzebni_vozidlo_lokalita
    ADD CONSTRAINT fk_sluzebni_vozidlo_lokalita_id_sluzebni_vozidlo FOREIGN KEY (id_sluzebni_vozidlo) REFERENCES vratnice.sluzebni_vozidlo(id_sluzebni_vozidlo);

ALTER TABLE vratnice.tmp_opravneni_vse
    ADD CONSTRAINT fk_tmp_opravneni_vse_authority FOREIGN KEY (authority) REFERENCES vratnice.role(authority);

ALTER TABLE vratnice.tmp_opravneni_vse
    ADD CONSTRAINT fk_tmp_opravneni_vse_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.tmp_opravneni_vyber
    ADD CONSTRAINT fk_tmp_opravneni_vyber_authority FOREIGN KEY (authority) REFERENCES vratnice.role(authority);

ALTER TABLE vratnice.tmp_opravneni_vyber
    ADD CONSTRAINT fk_tmp_opravneni_vyber_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.tmp_opravneni_vyber
    ADD CONSTRAINT fk_tmp_opravneni_vyber_id_uzivatel_podrizeny FOREIGN KEY (id_uzivatel_podrizeny) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT fk_uzivatel_id_kmenova_data FOREIGN KEY (id_kmenova_data) REFERENCES vratnice.kmenova_data(id_kmenova_data);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT fk_uzivatel_id_pracovni_pozice FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice(id_pracovni_pozice);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT fk_uzivatel_id_zakazka FOREIGN KEY (id_zakazka) REFERENCES vratnice.zakazka(id_zakazka);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT fk_uzivatel_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.uzivatel
    ADD CONSTRAINT fk_uzivatel_id_zavod_vyber FOREIGN KEY (id_zavod_vyber) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.uzivatel_modul
    ADD CONSTRAINT fk_uzivatel_modul_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.uzivatel_navstevni_listek_typ
    ADD CONSTRAINT fk_uzivatel_navstevni_listek_typ_id_navstevni_listek_typ FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ(id_navstevni_listek_typ);

ALTER TABLE vratnice.uzivatel_navstevni_listek_typ
    ADD CONSTRAINT fk_uzivatel_navstevni_listek_typ_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.uzivatel_opravneni
    ADD CONSTRAINT fk_uzivatel_opravneni_id_opravneni FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni(id_opravneni);

ALTER TABLE vratnice.uzivatel_zavod
    ADD CONSTRAINT fk_uzivatel_zavod_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.uzivatel_zavod
    ADD CONSTRAINT fk_uzivatel_zavod_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.vjezd_vozidla
    ADD CONSTRAINT fk_vjezd_vozidla_id_ridic FOREIGN KEY (id_ridic) REFERENCES vratnice.ridic(id_ridic);

ALTER TABLE vratnice.vjezd_vozidla
    ADD CONSTRAINT fk_vjezd_vozidla_id_vozidlo_typ FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ(id_vozidlo_typ);

ALTER TABLE vratnice.zadost_externi
    ADD CONSTRAINT fk_zadost_externi_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.zadost_externi
    ADD CONSTRAINT fk_zadost_externi_id_uzivatel_vytvoril FOREIGN KEY (id_uzivatel_vytvoril) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.zadost_externi_zaznam
    ADD CONSTRAINT fk_zadost_externi_zaznam_id_zadost_externi FOREIGN KEY (id_zadost_externi) REFERENCES vratnice.zadost_externi(id_zadost_externi);

ALTER TABLE vratnice.zadost_klic
    ADD CONSTRAINT fk_zadost_klic_id_klic FOREIGN KEY (id_klic) REFERENCES vratnice.klic(id_klic);

ALTER TABLE vratnice.zadost_klic
    ADD CONSTRAINT fk_zadost_klic_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.zakazka
    ADD CONSTRAINT fk_zakazka_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod);

ALTER TABLE vratnice.zastup
    ADD CONSTRAINT fk_zastup_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.zastup
    ADD CONSTRAINT fk_zastup_id_uzivatel_zastupce FOREIGN KEY (id_uzivatel_zastupce) REFERENCES vratnice.uzivatel(id_uzivatel);

ALTER TABLE vratnice.uzivatelske_nastaveni
    ADD CONSTRAINT uzivatelske_nastaveni_id_uzivatel_fkey FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel);

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 1, cas_zmn = now(), zmenu_provedl = 'pgadmin';