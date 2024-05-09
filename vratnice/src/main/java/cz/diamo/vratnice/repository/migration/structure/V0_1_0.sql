-- Založení typu pro práci se změnami
create type zmena as (
            sloupec text,
            old_value text,
            new_value text
        );

-- Tabulka pro nastavení databáze
create table vratnice.databaze (
  id_databaze integer not null,
  db_prefix VARCHAR(2) not null,
  verze_db smallint not null,
  sub_verze_db smallint not null,
  demo boolean not null default false,   
  poznamka VARCHAR(4000),
  cas_zmn timestamp (6) not null,
  zmenu_provedl VARCHAR(100),
  constraint pk_databaze primary key (id_databaze)   
); 
insert into vratnice.databaze (id_databaze, db_prefix,verze_db,sub_verze_db,cas_zmn,zmenu_provedl) 
	values (0, 'TK', 0, 1, now(),'pgadmin');

-- Tabulka pro zdrojové texty
CREATE SEQUENCE vratnice.seq_zdrojovy_text_id_zdrojovy_text INCREMENT 1 START 1;    
create table vratnice.zdrojovy_text (
     id_zdrojovy_text INTEGER not null DEFAULT NEXTVAL(('vratnice.seq_zdrojovy_text_id_zdrojovy_text'::text)::regclass),
     culture VARCHAR(100) not null default 'cs',
     hash VARCHAR(1000) not null,
     text text not null,
     UNIQUE (culture, hash),
     constraint pk_zdrojovy_text primary key (id_zdrojovy_text)
);
create unique index ix_zdrojovy_text_culture_hash on vratnice.zdrojovy_text (culture,  hash);
  
-- Tabulka pro role
create table vratnice.role (
     authority VARCHAR(100) not null,
     nazev_resx VARCHAR(100) not null,
constraint pk_role primary key (authority)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_ZAMESTNANEC','Zaměstnanec');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_ZAMESTNANEC', 'ROLE_ZAMESTNANEC');
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_OPRAVNENI','Správa oprávnění');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_OPRAVNENI', 'ROLE_SPRAVA_OPRAVNENI');
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_EXTERNICH_UZIVATELU','Správa externích uživatelů');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_EXTERNICH_UZIVATELU', 'ROLE_SPRAVA_EXTERNICH_UZIVATELU');
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_ZAVODU','Správa závodu');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_ZAVODU', 'ROLE_SPRAVA_ZAVODU');




-- Tabulka typu historie záznamů
create table vratnice.historie_zaznamu_typ (
     id_historie_zaznamu_typ INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_historie_zaznamu_typ primary key (id_historie_zaznamu_typ)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('TYP_HISTORIE_ZAZNAMU_NOVY','Nový');
insert into vratnice.historie_zaznamu_typ (id_historie_zaznamu_typ, nazev_resx) values (0, 'TYP_HISTORIE_ZAZNAMU_NOVY');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_HISTORIE_ZAZNAMU_ZMENA','Změna');
insert into vratnice.historie_zaznamu_typ (id_historie_zaznamu_typ, nazev_resx) values (1, 'TYP_HISTORIE_ZAZNAMU_ZMENA');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_HISTORIE_ZAZNAMU_SMAZANI','Smazání');
insert into vratnice.historie_zaznamu_typ (id_historie_zaznamu_typ, nazev_resx) values (2, 'TYP_HISTORIE_ZAZNAMU_SMAZANI');

-- Tabulka pro historii záznamů
CREATE SEQUENCE vratnice.seq_historie_zaznamu_id_historie_zaznamu INCREMENT 1 START 1;
create table vratnice.historie_zaznamu (
     id_historie_zaznamu bigint NOT NULL DEFAULT NEXTVAL(('vratnice.seq_historie_zaznamu_id_historie_zaznamu'::text)::regclass),     
     id_historie_zaznamu_typ INTEGER not null default 1,
     id_zaznamu VARCHAR(14) not null,
     table_name VARCHAR(100) not null, 
     table_column VARCHAR(100), 
     new_value VARCHAR(1000),
     old_value VARCHAR(1000),   
     zmenu_provedl VARCHAR(100),  
     zmenu_provedl_txt VARCHAR(1000),
     cas_zmn timestamp (6) not null,     
     constraint pk_historie_zaznamu primary key (id_historie_zaznamu)   
);

ALTER TABLE vratnice.historie_zaznamu ADD CONSTRAINT fk_historie_zaznamu_id_historie_zaznamu_typ
	FOREIGN KEY (id_historie_zaznamu_typ) REFERENCES vratnice.historie_zaznamu_typ (id_historie_zaznamu_typ) ON DELETE No Action ON UPDATE No Action
;

-- Tabulka závodů
CREATE SEQUENCE vratnice.seq_zavod_id_zavod INCREMENT 1 START 1;
create table vratnice.zavod (
     id_zavod VARCHAR(14) not null,
     nazev VARCHAR(1000) not null,
     sap_id VARCHAR(100) not null,
     barva_pozadi VARCHAR(10),
     barva_pisma VARCHAR(10),
     poznamka VARCHAR(4000),
     aktivita boolean not null default true,  
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_zavod primary key (id_zavod)   
);

create unique index ix_zavod_sap_id on vratnice.zavod (sap_id);

-- Insert závodů
insert into vratnice.zavod(id_zavod, nazev, sap_id, barva_pozadi, barva_pisma, cas_zmn) values ('XXZA0000000001', 'ŘSP', 'D100', '#8bb875', '#ffffff', now());
insert into vratnice.zavod(id_zavod, nazev, sap_id, barva_pozadi, barva_pisma, cas_zmn) values ('XXZA0000000006', 'HBZS', 'D600', '#f9c5af', '#ffffff', now());
insert into vratnice.zavod(id_zavod, nazev, sap_id, barva_pozadi, barva_pisma, cas_zmn) values ('XXZA0000000007', 'Darkov', 'D700', '#333831', '#ffffff', now());
insert into vratnice.zavod(id_zavod, nazev, sap_id, barva_pozadi, barva_pisma, cas_zmn) values ('XXZA0000000008', 'PKÚ', 'D800', '#7f9bcb', '#ffffff', now());

-- Pracovní pozice
CREATE SEQUENCE vratnice.seq_pracovni_pozice_id_pracovni_pozice INCREMENT 1 START 1;
create table vratnice.pracovni_pozice (
     id_pracovni_pozice VARCHAR(14) not null,
     nazev VARCHAR(1000) not null,
     zkratka VARCHAR(100) not null,
     sap_id VARCHAR(100) not null,
     sap_id_nadrizeny VARCHAR(100),
     platnost_od date not null default '1900-01-01 00:00',
     platnost_do date not null default '2999-12-31 23:59',
     cas_aktualizace timestamp (6),
     poznamka VARCHAR(4000),
     aktivita boolean not null default true,  
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_pracovni_pozice primary key (id_pracovni_pozice)   
);

create unique index ix_pracovni_pozice_sap_id on vratnice.pracovni_pozice (sap_id);

-- Tabulka podřízených pracovních pozic
CREATE SEQUENCE vratnice.seq_pracovni_pozice_podrizene_id_pracovni_pozice_podrizene INCREMENT 1 START 1;   
create table vratnice.pracovni_pozice_podrizene (
     id_pracovni_pozice_podrizene VARCHAR(14) not null,
     id_pracovni_pozice VARCHAR(14) not null,
     id_pracovni_pozice_podrizeny VARCHAR(14) not null,
     primy_podrizeny boolean not null default false, 
     poznamka VARCHAR(4000),
     aktivita boolean not null default true,
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_pracovni_pozice_podrizene primary key (id_pracovni_pozice_podrizene)  
);

ALTER TABLE vratnice.pracovni_pozice_podrizene ADD CONSTRAINT fk_pracovni_pozice_podrizene_id_pracovni_pozice
	FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice (id_pracovni_pozice) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.pracovni_pozice_podrizene ADD CONSTRAINT fk_pracovni_pozice_podrizene_id_pracovni_pozice_podrizeny
	FOREIGN KEY (id_pracovni_pozice_podrizeny) REFERENCES vratnice.pracovni_pozice (id_pracovni_pozice) ON DELETE No Action ON UPDATE No Action
;


-- Typ přístupu oprávnění
create table vratnice.opravneni_typ_pristupu (
     id_opravneni_typ_pristupu INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_opravneni_typ_pristupu primary key (id_opravneni_typ_pristupu)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_OPR_BEZ_PRISTUPU','Bez přístupu');
insert into vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu, nazev_resx) values (1, 'TYP_PRIST_OPR_BEZ_PRISTUPU');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_OPR_VSE','Vše');
insert into vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu, nazev_resx) values (2, 'TYP_PRIST_OPR_VSE');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_OPR_VYBER','Výběrem');
insert into vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu, nazev_resx) values (3, 'TYP_PRIST_OPR_VYBER');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_OPR_PRIMI_PODRIZENI','Přímí podřízení');
insert into vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu, nazev_resx) values (4, 'TYP_PRIST_OPR_PRIMI_PODRIZENI');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_OPR_VSICHNI_PODRIZENI','Všichni podřízení');
insert into vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu, nazev_resx) values (5, 'TYP_PRIST_OPR_VSICHNI_PODRIZENI');

-- Tabulka pro oprávnění
CREATE SEQUENCE vratnice.seq_opravneni_id_opravneni INCREMENT 1 START 1;
create table vratnice.opravneni (
    id_opravneni VARCHAR(14) not null,
    id_opravneni_typ_pristupu INTEGER not null,
    kod VARCHAR(100) not null, 
    nazev VARCHAR(100) not null,
    poznamka VARCHAR(4000),
    aktivita boolean not null default true,
    cas_zmn timestamp (6) not null,
    zmenu_provedl VARCHAR(100), 
    constraint pk_opravneni primary key (id_opravneni)   
);

ALTER TABLE vratnice.opravneni ADD CONSTRAINT fk_opravneni_id_opravneni_typ_pristupu
	FOREIGN KEY (id_opravneni_typ_pristupu) REFERENCES vratnice.opravneni_typ_pristupu (id_opravneni_typ_pristupu) ON DELETE No Action ON UPDATE No Action
;

-- Oprávnění-role
create table vratnice.opravneni_role (
      id_opravneni VARCHAR(14) not null,
      authority VARCHAR(100) not null,
      constraint pk_opravneni_role primary key (id_opravneni,authority)         
);

ALTER TABLE vratnice.opravneni_role ADD CONSTRAINT fk_opravneni_role_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.opravneni_role ADD CONSTRAINT fk_opravneni_role_authority
	FOREIGN KEY (authority) REFERENCES vratnice.role (authority) ON DELETE No Action ON UPDATE No Action
;

-- Oprávnění-pracovní pozice
create table vratnice.opravneni_pracovni_pozice (
      id_opravneni VARCHAR(14) not null,
      id_pracovni_pozice VARCHAR(100) not null,
      constraint pk_opravneni_pracovni_pozice primary key (id_opravneni,id_pracovni_pozice)         
);

ALTER TABLE vratnice.opravneni_pracovni_pozice ADD CONSTRAINT fk_opravneni_pracovni_pozice_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.opravneni_pracovni_pozice ADD CONSTRAINT fk_opravneni_pracovni_pozice_id_pracovni_pozice
	FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice (id_pracovni_pozice) ON DELETE No Action ON UPDATE No Action
;

-- Oprávnění-závod
create table vratnice.opravneni_zavod (
      id_opravneni VARCHAR(14) not null,
      id_zavod VARCHAR(100) not null,
      constraint pk_opravneni_zavod primary key (id_opravneni,id_zavod)         
);

ALTER TABLE vratnice.opravneni_zavod ADD CONSTRAINT fk_opravneni_zavod_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.opravneni_zavod ADD CONSTRAINT fk_opravneni_zavod_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
;

-- Založení oprávnění pro administrátora
insert into vratnice.opravneni(id_opravneni, id_opravneni_typ_pristupu, kod, nazev, cas_zmn) values ('XXOP0000000001', 2, 'vratnice_hlavni_administrator', 'Hlavní administrátor', now());
-- Oprávnění - role
insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_ZAMESTNANEC');
insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_SPRAVA_OPRAVNENI');
insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_SPRAVA_EXTERNICH_UZIVATELU');
insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_SPRAVA_ZAVODU');
-- Oprávnění - závod
insert into vratnice.opravneni_zavod(id_opravneni, id_zavod) values ('XXOP0000000001', 'XXZA0000000001');
insert into vratnice.opravneni_zavod(id_opravneni, id_zavod) values ('XXOP0000000001', 'XXZA0000000006');
insert into vratnice.opravneni_zavod(id_opravneni, id_zavod) values ('XXOP0000000001', 'XXZA0000000007');
insert into vratnice.opravneni_zavod(id_opravneni, id_zavod) values ('XXOP0000000001', 'XXZA0000000008');

-- Kmenová data SAP
CREATE SEQUENCE vratnice.seq_kmenova_data_id_kmenova_data INCREMENT 1 START 1;
create table vratnice.kmenova_data (
     id_kmenova_data VARCHAR(14) not null,
     guid_davky VARCHAR(100) not null,  
     id_zavod VARCHAR(14) not null,     
     sap_id VARCHAR(100) not null,
     druh_vyneti_sap_id VARCHAR(100),
     druh_prac_pomeru_sap_id VARCHAR(100),
     forma_mzdy_sap_id VARCHAR(100),
     kategorie_sap_id VARCHAR(100),
     kalendar_sap_id VARCHAR(100),
     zakazka_sap_id VARCHAR(100),
     personalni_oblast_sap_id VARCHAR(100),
     dilci_personalni_oblast_sap_id VARCHAR(100),
     zauctovaci_okruh_sap_id VARCHAR(100),
     orgnizacni_jednotka_sap_id VARCHAR(100),
     planovane_misto_sap_id VARCHAR(100),
     skupina_zamestnance_sap_id VARCHAR(100),
     platnost_ke_dni date not null,
     cislo_znamky VARCHAR(20),
     rodne_cislo VARCHAR(11),
     jmeno VARCHAR(100) not null,
     prijmeni VARCHAR(100) not null,
     titul_pred VARCHAR(100),
     titul_za VARCHAR(100),
     ulice VARCHAR(400),
     psc VARCHAR(400),
     obec VARCHAR(400),     
     cislo_popisne VARCHAR(100),
     tel VARCHAR(1000),
     email VARCHAR(1000),
     soukromy_email VARCHAR(1000),
     datum_ukonceni_prac_pomeru date,
     denni_uvazek numeric(15,2) NOT NULL,     
     narok_na_dovolenou numeric(15,2) NOT NULL,
     zbytek_dovolene_minuly_rok numeric(15,2),
     dodatkova_dovolena numeric(15,2),
     cerpani_dovolene numeric(15,2) NOT NULL,
     prumer_pro_nahrady numeric(15,2) NOT NULL,
     pruzna_prac_doba boolean not null default false,
     zpracovano boolean not null default false, 
     chyba VARCHAR(4000),
     poznamka VARCHAR(4000),
     aktivita boolean not null default true,
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_kmenova_data primary key (id_kmenova_data)   
);

ALTER TABLE vratnice.kmenova_data ADD CONSTRAINT fk_kmenova_data_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
; 

-- Tabulka uživatelů
CREATE SEQUENCE vratnice.seq_uzivatel_id_uzivatel INCREMENT 1 START 1;
create table vratnice.uzivatel (
     id_uzivatel VARCHAR(14) not null,
     id_zavod VARCHAR(14) not null, 
     id_kmenova_data VARCHAR(14), 
     sap_id VARCHAR(100) not null,
     id_pracovni_pozice VARCHAR(14),       
     jmeno VARCHAR(100),
     prijmeni VARCHAR(100),
     titul_pred VARCHAR(100),
     titul_za VARCHAR(100),
     email VARCHAR(1000),
     soukromy_email VARCHAR(1000),
     tel VARCHAR(1000),
     nazev VARCHAR(1000),
     datum_od date NOT NULL,
     datum_do date,
     platnost_ke_dni timestamp (6),
     cas_aktualizace timestamp (6),
     ukonceno boolean not null default false,
     poznamka VARCHAR(4000),
     aktivita boolean not null default true,  
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_uzivatel primary key (id_uzivatel)   
);

create unique index ix_uzivatel_sap_id on vratnice.uzivatel (sap_id);

ALTER TABLE vratnice.uzivatel ADD CONSTRAINT fk_uzivatel_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.uzivatel ADD CONSTRAINT fk_uzivatel_id_kmenova_data
	FOREIGN KEY (id_kmenova_data) REFERENCES vratnice.kmenova_data (id_kmenova_data) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.uzivatel ADD CONSTRAINT fk_uzivatel_id_pracovni_pozice
	FOREIGN KEY (id_pracovni_pozice) REFERENCES vratnice.pracovni_pozice (id_pracovni_pozice) ON DELETE No Action ON UPDATE No Action
;

-- Uživatel-oprávnění
create table vratnice.uzivatel_opravneni (
      id_uzivatel VARCHAR(14) not null,
      id_opravneni VARCHAR(14) not null,
      constraint pk_uzivatel_opravneni primary key (id_uzivatel,id_opravneni)         
);

ALTER TABLE vratnice.uzivatel_opravneni ADD CONSTRAINT fk_ouzivatel_opravneni_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.uzivatel_opravneni ADD CONSTRAINT fk_uzivatel_opravneni_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

-- Externí uživatele
CREATE SEQUENCE vratnice.seq_externi_uzivatel_id_externi_uzivatel INCREMENT 1 START 1;
create table vratnice.externi_uzivatel (
     id_externi_uzivatel VARCHAR(14) not null,
     nazev VARCHAR(1000) not null, 
     username VARCHAR(100) not null,
     password VARCHAR(100) not null,
     poznamka VARCHAR(4000),
     aktivita boolean not null default true, 
     cas_zmn timestamp (6) not null,
     zmenu_provedl VARCHAR(100), 
     constraint pk_externi_uzivatel primary key (id_externi_uzivatel)   
);

create unique index ix_externi_uzivatel_username on vratnice.externi_uzivatel (username);

-- Role externích uživatelu
create table vratnice.externi_role (
     authority VARCHAR(100) not null,
     nazev_resx VARCHAR(100) not null,
constraint pk_externi_role primary key (authority)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('EXT_ROLE_PERSONALISTIKA','Personalistika');
insert into vratnice.externi_role (authority, nazev_resx) values ('ROLE_PERSONALISTIKA', 'EXT_ROLE_PERSONALISTIKA');

-- Authority-ext. uživatele
create table vratnice.externi_uzivatel_role (
      id_externi_uzivatel VARCHAR(14) not null,
      authority VARCHAR(100) not null,
      constraint pk_externi_uzivatel_role primary key (id_externi_uzivatel,authority)         
);

ALTER TABLE vratnice.externi_uzivatel_role ADD CONSTRAINT fk_externi_uzivatel_role_id_externi_uzivatel
	FOREIGN KEY (id_externi_uzivatel) REFERENCES vratnice.externi_uzivatel (id_externi_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.externi_uzivatel_role ADD CONSTRAINT fk_externi_uzivatel_role_authority
	FOREIGN KEY (authority) REFERENCES vratnice.externi_role (authority) ON DELETE No Action ON UPDATE No Action
;

-- Založení externího uživatele
insert into vratnice.externi_uzivatel (id_externi_uzivatel, nazev, username, password, cas_zmn) values ('XXEU0000000001', 'ext1', 'ext1', '$2a$12$fhYpbOJ/3jpSnksn60Lp2.FWOIXpAt4gt/TSkpQwXpAzfsZIElaH.', now());
insert into vratnice.externi_uzivatel_role (id_externi_uzivatel, authority) values ('XXEU0000000001', 'ROLE_PERSONALISTIKA');

-- TMP tabulka pro přístup ke všem zaměstnancům  
create table vratnice.tmp_opravneni_vse (
    id_tmp_opravneni_vse VARCHAR(200) not null,
    id_uzivatel VARCHAR(14) not null,
    authority VARCHAR(100) not null,
    constraint pk_tmp_opravneni_vse primary key (id_tmp_opravneni_vse)   
);

ALTER TABLE vratnice.tmp_opravneni_vse  ADD CONSTRAINT fk_tmp_opravneni_vse_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.tmp_opravneni_vse  ADD CONSTRAINT fk_tmp_opravneni_vse_authority
	FOREIGN KEY (authority) REFERENCES vratnice.role (authority) ON DELETE No Action ON UPDATE No Action
;

-- TMP tabulka pro přístup k vybraným zaměstnancům  
create table vratnice.tmp_opravneni_vyber (
    id_tmp_opravneni_vyber VARCHAR(200) not null,
    id_uzivatel VARCHAR(14) not null,
    id_uzivatel_podrizeny VARCHAR(14) not null,
    authority VARCHAR(100) not null,
    constraint pk_tmp_opravneni_vyber primary key (id_tmp_opravneni_vyber)   
);

ALTER TABLE vratnice.tmp_opravneni_vyber  ADD CONSTRAINT fk_tmp_opravneni_vyber_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.tmp_opravneni_vyber  ADD CONSTRAINT fk_tmp_opravneni_vyber_id_uzivatel_podrizeny
	FOREIGN KEY (id_uzivatel_podrizeny) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.tmp_opravneni_vyber  ADD CONSTRAINT fk_tmp_opravneni_vyber_authority
	FOREIGN KEY (authority) REFERENCES vratnice.role (authority) ON DELETE No Action ON UPDATE No Action
;




-- Logování

-- Logování uživatele
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

-- Doplnění textu pro popis sloupců
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_NAZEV','Název');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_JMENO','Jméno');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_PRIJMENI','Příjmení');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_TITUL_PRED','Titul před');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_TITUL_ZA','Titul za');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_EMAIL','E-mail');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_SOUKROMY_EMAIL','Soukromý e-mail');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_POZNAMKA','Poznámka');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_AKTIVITA','Aktivita');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_KMENOVA_DATA','Kmenová data');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_PLATNOST_KE_DNI','Platnost ke dni');

-- Trigger pro zakládání logu
CREATE TRIGGER uzivatel_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.uzivatel
    FOR EACH ROW EXECUTE PROCEDURE uzivatel_log();

-- Logování uživatel-oprávnění
CREATE OR REPLACE FUNCTIon vratnice.uzivatel_opravneni_log() RETURNS trigger AS $uzivatel_opravneni_log$

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
$uzivatel_opravneni_log$ LANGUAGE plpgsql;


-- Trigger pro zakládání logu
CREATE TRIGGER uzivatel_opravneni_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.uzivatel_opravneni
    FOR EACH ROW EXECUTE PROCEDURE uzivatel_opravneni_log();

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_OPRAVNENI','Oprávnění');

-- Logování oprávnění
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

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_OPRAVNENI_TYP_PRISTUPU','Omezení přístupu oprávnění k zaměstnancům');
insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_KOD','Kód');

-- Trigger pro zakládání logu
CREATE TRIGGER opravneni_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.opravneni
    FOR EACH ROW EXECUTE PROCEDURE opravneni_log();


-- Logování oprávnění-role
CREATE OR REPLACE FUNCTIon vratnice.opravneni_role_log() RETURNS trigger AS $opravneni_role_log$

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
$opravneni_role_log$ LANGUAGE plpgsql;

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_AUTHORITY','Role');

-- Trigger pro zakládání logu
CREATE TRIGGER opravneni_role_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.opravneni_role
    FOR EACH ROW EXECUTE PROCEDURE opravneni_role_log();

-- Logování oprávnění-závod
CREATE OR REPLACE FUNCTIon vratnice.opravneni_zavod_log() RETURNS trigger AS $opravneni_zavod_log$

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
$opravneni_zavod_log$ LANGUAGE plpgsql;

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_ZAVOD','Závod');

-- Trigger pro zakládání logu
CREATE TRIGGER opravneni_zavod_log_trg BEFORE INSERT OR DELETE on vratnice.opravneni_zavod
    FOR EACH ROW EXECUTE PROCEDURE opravneni_zavod_log();

-- Logování oprávnění-pracovní pozice
CREATE OR REPLACE FUNCTIon vratnice.opravneni_pracovni_pozice_log() RETURNS trigger AS $opravneni_pracovni_pozice_log$

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
$opravneni_pracovni_pozice_log$ LANGUAGE plpgsql;

insert into vratnice.zdrojovy_text (hash, text) values ('COLUMN_NAME_ID_PRACOVNI_POZICE','Praacovní pozice');

-- Trigger pro zakládání logu
CREATE TRIGGER opravneni_pracovni_pozice_log_trg AFTER INSERT OR UPDATE OR DELETE on vratnice.opravneni_pracovni_pozice
    FOR EACH ROW EXECUTE PROCEDURE opravneni_pracovni_pozice_log();

-- Tabulka pro uživatelské nastavení 
CREATE SEQUENCE vratnice.seq_uzivatelske_nastaveni_id_uzivatelske_nastaveni INCREMENT 1 START 1;
create table vratnice.uzivatelske_nastaveni(
     id_uzivatelske_nastaveni VARCHAR(14) not null,
     id_uzivatel VARCHAR(14) not null,
     klic VARCHAR(100),
     hodnota VARCHAR(4000), 
constraint pk_uzivatelske_nastaveni primary key (id_uzivatelske_nastaveni)   
); 

alter table vratnice.uzivatelske_nastaveni add foreign key ( id_uzivatel ) references vratnice.uzivatel (id_uzivatel) on delete no action on update no action;

create unique index ix_uzivatelske_nastaveni_id_uzivatel_klic on vratnice.uzivatelske_nastaveni (id_uzivatel, klic);

-- Tabulka pro stav dlouhotrvající operace
create table vratnice.tmp_stav_operace (
     id_uzivatel VARCHAR(14) not null,
     id_operace VARCHAR(1000) not null,
     title VARCHAR(100) not null,
     message text,
     status VARCHAR(100) not null,
     size integer,
     actual integer,
     progress integer,    
     start timestamp (6) not null,
     cas timestamp (6) not null,
     trvani integer,
     constraint pk_tmp_stav_operace primary key (id_uzivatel, id_operace)              
);

-- Tabulka zakazka
CREATE SEQUENCE vratnice.seq_zakazka_id_zakazka INCREMENT 1 START 1;
CREATE TABLE vratnice.zakazka (
	id_zakazka varchar(14) NOT NULL,
	id_zavod varchar(14) NOT NULL,
	nazev varchar(1000) NOT NULL,
	sap_id varchar(100) NOT NULL,
	platnost_od date NOT NULL,
	platnost_do date NOT NULL,
	poznamka varchar(4000) NULL,
	aktivita boolean NOT NULL DEFAULT true,
	cas_zmn timestamp(6) NOT NULL,
	zmenu_provedl varchar(100) NULL,
	virtualni boolean NOT NULL DEFAULT false,
	CONSTRAINT pk_zakazka PRIMARY KEY (id_zakazka)
);

ALTER TABLE vratnice.zakazka ADD CONSTRAINT fk_zakazka_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action
;

create unique index ix_zakazka_id_zavod_sap_id on vratnice.zakazka (id_zavod, sap_id);

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 1, cas_zmn = now(), zmenu_provedl = 'pgadmin';