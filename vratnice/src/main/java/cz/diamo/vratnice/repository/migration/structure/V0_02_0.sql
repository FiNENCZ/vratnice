-- 74
CREATE SEQUENCE vratnice.seq_inicializace_vratnice_kamery_id_inicializace_vratnice_kamery INCREMENT 1 START 1;

-- Vytvoření tabulky inicializace_vratnice_kamery
CREATE TABLE vratnice.inicializace_vratnice_kamery (
    id_inicializace_vratnice_kamery VARCHAR(14) NOT NULL,
    ip_adresa VARCHAR(20) NOT NULL,
    cas_inicializace TIMESTAMP NOT NULL,

    CONSTRAINT pk_inicializace_vratnice_kamery PRIMARY KEY (id_inicializace_vratnice_kamery)
);

-- 75
ALTER TABLE vratnice.inicializace_vratnice_kamery
ADD CONSTRAINT uq_ip_adresa UNIQUE (ip_adresa);

-- 76
CREATE SEQUENCE vratnice.seq_vratnice_id_vratnice INCREMENT 1 START 1;

CREATE TABLE vratnice.vratnice (
    id_vratnice varchar(14) NOT NULL,
    nazev varchar(50) NOT NULL,
    id_zavod varchar(14) NOT NULL,
    id_lokalita varchar(14) NOT NULL,
    osobni boolean DEFAULT false NOT NULL,
    navstevni boolean DEFAULT false NOT NULL,
    vjezdova boolean DEFAULT false NOT NULL,
    id_navstevni_listek_typ int4 NOT NULL,
    odchozi_turniket boolean DEFAULT false NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_vratnice PRIMARY KEY (id_vratnice),
    CONSTRAINT fk_vratnice_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_vratnice_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita(id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_vratnice_id_navstevni_listek_typ FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ(id_navstevni_listek_typ) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX ix_vratnice_id_zavod ON vratnice.vratnice (id_zavod);
CREATE INDEX ix_vratnice_id_lokalita ON vratnice.vratnice (id_lokalita);
CREATE INDEX ix_vratnice_id_navstevni_listek_typ ON vratnice.vratnice (id_navstevni_listek_typ);

-- 77
CREATE SEQUENCE vratnice.seq_uzivatel_vratnice_id_uzivatel_vratnice INCREMENT 1 START 1;

-- Vytvoření tabulky NavstevniListek
CREATE TABLE IF NOT EXISTS vratnice.uzivatel_vratnice (
    id_uzivatel_vratnice VARCHAR(14) NOT NULL,
    id_uzivatel varchar(14) NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_uzivatel_vratnice PRIMARY KEY (id_uzivatel_vratnice),
    CONSTRAINT fk_uzivatel_vratnice_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_uzivatel_vratnice_id_uzivatel ON vratnice.uzivatel (id_uzivatel);

-- Vytvoření tabulky uzivatel_vratnice_mapovani pro Many-to-Many vztah s entitou NavstevaOsoba
CREATE TABLE IF NOT EXISTS vratnice.uzivatel_vratnice_mapovani (
    id_uzivatel_vratnice VARCHAR(14) NOT NULL,
    id_vratnice VARCHAR(14) NOT NULL,
    CONSTRAINT pk_uzivatel_vratnice_mapovani  PRIMARY KEY (id_uzivatel_vratnice, id_vratnice),
    CONSTRAINT fk_uzivatel_vratnice_mapovani_id_uzivatel_vratnice FOREIGN KEY (id_uzivatel_vratnice) REFERENCES vratnice.uzivatel_vratnice (id_uzivatel_vratnice) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_uzivatel_vratnice_mapovani_id_vratnice FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_uzivatel_vratnice_mapovani_id_uzivatel_vratnice ON vratnice.uzivatel_vratnice_mapovani (id_uzivatel_vratnice);
CREATE INDEX ix_uzivatel_vratnice_mapovani_id_vratnice ON vratnice.uzivatel_vratnice_mapovani (id_vratnice);

-- 78
ALTER TABLE vratnice.uzivatel_vratnice
    ADD CONSTRAINT uq_uzivatel_vratnice_id_uzivatel UNIQUE (id_uzivatel);

-- 79
ALTER TABLE vratnice.uzivatel_vratnice
    ADD COLUMN nastavena_vratnice VARCHAR(14);

ALTER TABLE vratnice.uzivatel_vratnice
    ADD CONSTRAINT fk_uzivatel_vratnice_nastavena_vratnice

FOREIGN KEY (nastavena_vratnice) REFERENCES vratnice.vratnice(id_vratnice)
    ON DELETE NO ACTION ON UPDATE NO ACTION;

-- 80
ALTER TABLE vratnice.vjezd_vozidla
    ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.vjezd_vozidla ADD CONSTRAINT fk_vjezd_vozidla_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

ALTER TABLE vratnice.vyjezd_vozidla
    ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.vyjezd_vozidla ADD CONSTRAINT fk_vyjezd_vozidla_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

-- 81
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_VSECHNY_VRATNICE','Přístup ke všem vrátnicím');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_VSECHNY_VRATNICE', 'ROLE_VSECHNY_VRATNICE');

-- 82
create table vratnice.uzivatel_vsechny_vratnice (
     id_uzivatel VARCHAR(14) not null,
     aktivni_vsechny_vratnice BOOLEAN not null,
     constraint pk_uzivatel_vsechny_vratnice primary key (id_uzivatel)   
);

ALTER TABLE vratnice.uzivatel_vsechny_vratnice ADD CONSTRAINT fk_uzivatel_vsechny_vratnice_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action;

-- 83
ALTER TABLE vratnice.klic
ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.klic
    SET id_vratnice = 'TKSU0000000001';

-- 84
ALTER TABLE vratnice.navstevni_listek
    ADD COLUMN id_vratnice VARCHAR(14);

ALTER TABLE vratnice.navstevni_listek ADD CONSTRAINT fk_navstevni_listek_id_vratnice
	FOREIGN KEY (id_vratnice) REFERENCES vratnice.vratnice (id_vratnice) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.navstevni_listek
    SET id_vratnice = 'TKSU0000000001';

-- 85
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'VOZIDLO_OSOBNI','personal');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'VOZIDLO_DODAVKA','van');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'VOZIDLO_NAKLADNI','truck');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'VOZIDLO_SPECIALNI','special');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'VOZIDLO_IZS','IZS');

-- 86
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'STAT_CESKA_REPUBLIKA','Czech Republic');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'STAT_SLOVENSKO','Slovakia');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'STAT_POLSKO','Poland');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'STAT_NEMECKO','Germany');
insert into vratnice.zdrojovy_text (culture, hash, text) values ('en', 'STAT_RAKOUSKO','Austria');

-- 87
create table vratnice.historie_klic_akce (
     id_historie_klic_akce INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_historie_klic_akce primary key (id_historie_klic_akce)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_VYTVOREN','vytvořen');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (1, 'HISTORIE_KLIC_AKCE_VYTVOREN');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_UPRAVEN','upraven');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (2, 'HISTORIE_KLIC_AKCE_UPRAVEN');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_ODSTRANEN','odstraněn');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (3, 'HISTORIE_KLIC_AKCE_ODSTRANEN');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_BLOKOVAN','blokován');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (4, 'HISTORIE_KLIC_AKCE_BLOKOVAN');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_OBNOVEN','obnoven');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (5, 'HISTORIE_KLIC_AKCE_OBNOVEN');

CREATE SEQUENCE vratnice.seq_historie_klic_id_historie_klic INCREMENT 1 START 1;


CREATE TABLE vratnice.historie_klic (
	id_historie_klic varchar(14) NOT NULL,
	id_klic varchar(14) NOT NULL,
	datum timestamp NOT NULL,
	id_uzivatel varchar(14) NULL,
	id_historie_klic_akce int4 NULL,
	CONSTRAINT pk_historie_klic PRIMARY KEY (id_historie_klic),
	CONSTRAINT fk_historie_klic_id_historie_klic_akce FOREIGN KEY (id_historie_klic_akce) REFERENCES vratnice.historie_klic_akce(id_historie_klic_akce),
	CONSTRAINT fk_historie_klic_id_klic FOREIGN KEY (id_klic) REFERENCES vratnice.klic(id_klic),
	CONSTRAINT fk_historie_klic_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel)
);

CREATE INDEX ix_historie_klic_id_klic ON vratnice.historie_klic USING btree (id_klic);
CREATE INDEX ix_historie_klic_id_uzivatel ON vratnice.historie_klic USING btree (id_uzivatel);
CREATE INDEX ix_historie_klic_id_historie_klic_akce ON vratnice.historie_klic USING btree (id_historie_klic);

-- 88
ALTER TABLE vratnice.historie_klic
    ADD COLUMN duvod varchar(4000) NULL;

insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_VYMENA','výměna');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (6, 'HISTORIE_KLIC_AKCE_VYMENA');

-- 89
CREATE SEQUENCE vratnice.seq_spolecnost_id_spolecnost INCREMENT 1 START 1;

CREATE TABLE vratnice.spolecnost (
    id_spolecnost VARCHAR(14) NOT NULL,
    nazev VARCHAR(80) NOT NULL,
    CONSTRAINT pk_spolecnost PRIMARY KEY (id_spolecnost)
);

-- 90
ALTER TABLE vratnice.najemnik_navstevnicka_karta 
    DROP COLUMN spolecnost;


ALTER TABLE vratnice.najemnik_navstevnicka_karta
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.najemnik_navstevnicka_karta ADD CONSTRAINT fk_najemnik_navstevnicka_karta_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.najemnik_navstevnicka_karta
    SET id_spolecnost = 'TKSP0000000001';

-- 91
ALTER TABLE vratnice.ridic 
    DROP COLUMN firma;


ALTER TABLE vratnice.ridic
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.ridic ADD CONSTRAINT fk_ridic_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.ridic
    SET id_spolecnost = 'TKSP0000000001';


ALTER TABLE vratnice.navsteva_osoba 
    DROP COLUMN firma;

ALTER TABLE vratnice.navsteva_osoba
    ADD COLUMN id_spolecnost VARCHAR(14);

ALTER TABLE vratnice.navsteva_osoba ADD CONSTRAINT fk_navsteva_osoba_id_spolecnost
	FOREIGN KEY (id_spolecnost) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.navsteva_osoba
    SET id_spolecnost = 'TKSP0000000001';

-- 92
CREATE SEQUENCE vratnice.seq_specialni_klic_oznameni_vypujcky_id_specialni_klic_oznameni_vypujcky INCREMENT 1 START 1;

-- Vytvoření tabulky NavstevniListek
CREATE TABLE IF NOT EXISTS vratnice.specialni_klic_oznameni_vypujcky (
    id_specialni_klic_oznameni_vypujcky VARCHAR(14) NOT NULL,
    id_klic varchar(14) NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_specialni_klic_oznameni_vypujcky PRIMARY KEY (id_specialni_klic_oznameni_vypujcky),
    CONSTRAINT fk_specialni_klic_oznameni_vypujcky_id_klic FOREIGN KEY (id_klic) REFERENCES vratnice.klic(id_klic) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT uq_specialni_klic_oznameni_vypujcky_id_klic UNIQUE (id_klic)
);

CREATE INDEX ix_specialni_klic_oznameni_vypujcky_id_klic ON vratnice.klic (id_klic);

CREATE TABLE IF NOT EXISTS vratnice.specialni_klic_oznameni_uzivatel (
    id_specialni_klic_oznameni_vypujcky VARCHAR(14) NOT NULL,
    id_uzivatel VARCHAR(14) NOT NULL,
    CONSTRAINT pk_specialni_klic_oznameni_uzivatel  PRIMARY KEY (id_specialni_klic_oznameni_vypujcky, id_uzivatel),
    CONSTRAINT fk_specialni_klic_oznameni_uzivatel_id_specialni_klic_oznameni_vypujcky FOREIGN KEY (id_specialni_klic_oznameni_vypujcky) REFERENCES vratnice.specialni_klic_oznameni_vypujcky  (id_specialni_klic_oznameni_vypujcky) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_specialni_klic_oznameni_uzivatel_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_specialni_klic_oznameni_uzivatel_id_specialni_klic_oznameni_vypujcky ON vratnice.specialni_klic_oznameni_uzivatel (id_specialni_klic_oznameni_vypujcky);
CREATE INDEX ix_specialni_klic_oznameni_uzivatel_id_uzivatel ON vratnice.specialni_klic_oznameni_uzivatel (id_uzivatel);

-- 93
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_VRANTNICE_KAMERY','Správa kamer vrátnic');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_VRANTNICE_KAMERY', 'ROLE_SPRAVA_VRANTNICE_KAMERY');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_VRATNI','Správa vrátných');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_VRATNI', 'ROLE_SPRAVA_VRATNI');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_VRATNICE','Správa vrátnic');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_VRATNICE', 'ROLE_SPRAVA_VRATNICE');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_NAJEMNICI_KARTY','Správa nájemníků návštěvních karet');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_NAJEMNICI_KARTY', 'ROLE_SPRAVA_NAJEMNICI_KARTY');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_DOCHAZKA','Správa docházky zaměstnanců');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_DOCHAZKA', 'ROLE_SPRAVA_DOCHAZKA');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_NAVSTEVNI_LISTEK','Správa návštěvních lístků');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_NAVSTEVNI_LISTEK', 'ROLE_SPRAVA_NAVSTEVNI_LISTEK');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_SLUZEBNI_VOZIDLO','Správa služebních vozidel');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_SLUZEBNI_VOZIDLO', 'ROLE_SPRAVA_SLUZEBNI_VOZIDLO');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_VJEZD_VOZIDEL','Správa vjezdu vozidel');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_VJEZD_VOZIDEL', 'ROLE_SPRAVA_VJEZD_VOZIDEL');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_VYJEZD_VOZIDEL','Správa výjezdu vozidel');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_VYJEZD_VOZIDEL', 'ROLE_SPRAVA_VYJEZD_VOZIDEL');

insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_RIDIC','Správa řidičů');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_RIDIC', 'ROLE_SPRAVA_RIDIC');

-- 94
-- Přejmenování správy externích uživatelů na správu externích systémů
update vratnice.zdrojovy_text set text = 'Správa externích systémů' where hash = 'ROLE_SPRAVA_EXTERNICH_UZIVATELU';

-- Doplněn příznak externího uživatele
ALTER TABLE vratnice.uzivatel ADD externi boolean not null default false;

-- Nová role pro správu vytváření nových externích uživatelů
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_UZIVATELU_EXT','Správce externích uživatelů');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_UZIVATELU_EXT', 'ROLE_SPRAVA_UZIVATELU_EXT');

-- 95
-- Nejprve zrušíme indexy, které jsou spojeny s tabulkou
DROP INDEX IF EXISTS vratnice.ix_povoleni_vjezdu_vozidla_zavod_id_povoleni_vjezdu_vozidla;
DROP INDEX IF EXISTS vratnice.ix_povoleni_vjezdu_vozidla_zavod_id_zavod;

-- Následně odstraníme tabulku, čímž se automaticky zruší i primární klíč a cizí klíče
DROP TABLE IF EXISTS vratnice.povoleni_vjezdu_vozidla_zavod CASCADE;


-- Vytvoření tabulky ManyToMany pro povoleni - lokalita
CREATE TABLE IF NOT EXISTS vratnice.povoleni_vjezdu_vozidla_lokalita (
    id_povoleni_vjezdu_vozidla VARCHAR(14) NOT NULL,
    id_lokalita VARCHAR(14) NOT NULL,
    CONSTRAINT pk_povoleni_vjezdu_vozidla_lokalita  PRIMARY KEY (id_povoleni_vjezdu_vozidla, id_lokalita),
    CONSTRAINT fk_povoleni_vjezdu_vozidla_lokalita_id_povoleni_vjezdu_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla) REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_povoleni_vjezdu_vozidla_lokalita_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita (id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_lokalita_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_lokalita (id_povoleni_vjezdu_vozidla);
CREATE INDEX ix_povoleni_vjezdu_vozidla_lokalita_id_lokalita ON vratnice.povoleni_vjezdu_vozidla_lokalita (id_lokalita);


-- Úprava společnosti žadatele
ALTER TABLE vratnice.povoleni_vjezdu_vozidla 
    DROP COLUMN spolecnost_zadatele;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN spolecnost_zadatele VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_spolecnost_zadatele
	FOREIGN KEY (spolecnost_zadatele) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET spolecnost_zadatele = 'TKSP0000000001';


-- Úprava společnosti vozidla
ALTER TABLE vratnice.povoleni_vjezdu_vozidla 
    DROP COLUMN spolecnost_vozidla;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN spolecnost_vozidla VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_spolecnost_vozidla
	FOREIGN KEY (spolecnost_vozidla) REFERENCES vratnice.spolecnost (id_spolecnost) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET spolecnost_vozidla = 'TKSP0000000001';

-- Přidání sloupce email
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN email_zadatele VARCHAR(255);

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET email_zadatele = 'test@gmail.com';


-- přidání sloupce id_zavod
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN id_zavod VARCHAR(14);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_zavod
	FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod (id_zavod) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET id_zavod = 'XXZA0000000001';

-- 96
-- Tabulka stavu žádosti
create table vratnice.zadost_stav (
     id_zadost_stav INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_zadost_stav primary key (id_zadost_stav)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('STAV_ZADOST_SCHVALENO','Schváleno');
insert into vratnice.zadost_stav (id_zadost_stav, nazev_resx) values (1, 'STAV_ZADOST_SCHVALENO');
insert into vratnice.zdrojovy_text (hash, text) values ('STAV_ZADOST_POZASTAVENO','Pozastaveno');
insert into vratnice.zadost_stav (id_zadost_stav, nazev_resx) values (2, 'STAV_ZADOST_POZASTAVENO');
insert into vratnice.zdrojovy_text (hash, text) values ('STAV_ZADOST_UKONCENO','Ukončeno');
insert into vratnice.zadost_stav (id_zadost_stav, nazev_resx) values (3, 'STAV_ZADOST_UKONCENO');

ALTER TABLE vratnice.zadost_klic RENAME COLUMN stav TO id_zadost_stav;
ALTER TABLE vratnice.zadost_klic ALTER COLUMN id_zadost_stav TYPE integer USING id_zadost_stav::integer;

ALTER TABLE vratnice.zadost_klic ADD CONSTRAINT fk_zadost_id_zadost_stav
	FOREIGN KEY (id_zadost_stav) REFERENCES vratnice.zadost_stav (id_zadost_stav) ON DELETE No Action ON UPDATE No Action;

-- 97
update vratnice.lokalita set id_externi = id_lokalita, cas_zmn = now(), zmenu_provedl = 'pgadmin' where id_externi is null;

ALTER TABLE vratnice.lokalita RENAME COLUMN id_externi TO kod;
ALTER TABLE vratnice.lokalita ALTER COLUMN kod TYPE varchar(100) USING kod::varchar(100);
ALTER TABLE vratnice.lokalita ALTER COLUMN kod SET NOT NULL;
ALTER TABLE vratnice.lokalita ALTER COLUMN nazev TYPE varchar(1000) USING nazev::varchar(1000);

-- Číselník typů přístupů k budovám - oprávnění
CREATE TABLE vratnice.opravneni_typ_pristupu_budova (
    id_opravneni_typ_pristupu_budova INTEGER not null,
    nazev_resx VARCHAR(100) not null,
    constraint pk_opravneni_typ_pristupu_budova primary key (id_opravneni_typ_pristupu_budova)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU','Bez přístupu');
insert into vratnice.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (1, 'TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_VSE','Vše');
insert into vratnice.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (2, 'TYP_PRIST_BUDOVA_OPR_VSE');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_VYBER','Výběrem');
insert into vratnice.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (3, 'TYP_PRIST_BUDOVA_OPR_VYBER');
insert into vratnice.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_ZAVOD','Dle závodu');
insert into vratnice.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (4, 'TYP_PRIST_BUDOVA_OPR_ZAVOD');

-- Doplnění typu přístupu na oprávnění
ALTER TABLE vratnice.opravneni ADD id_opravneni_typ_pristupu_budova INTEGER not null default 1;
ALTER TABLE vratnice.opravneni ADD CONSTRAINT fk_opravneni_id_opravneni_typ_pristupu_budova
	FOREIGN KEY (id_opravneni_typ_pristupu_budova) REFERENCES vratnice.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova) ON DELETE No Action ON UPDATE No Action;

-- Oprávnění - budova
create table vratnice.opravneni_budova (
      id_opravneni VARCHAR(14) not null,
      id_budova VARCHAR(14) not null,
      constraint pk_opravneni_budova primary key (id_opravneni, id_budova)         
);

ALTER TABLE vratnice.opravneni_budova ADD CONSTRAINT fk_opravneni_budova_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES vratnice.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.opravneni_budova ADD CONSTRAINT fk_opravneni_budova_id_budova
	FOREIGN KEY (id_budova) REFERENCES vratnice.budova (id_budova) ON DELETE No Action ON UPDATE No Action
;

-- Doplnění přístupu k budovám pro oprávnění hlavní administrátor
update vratnice.opravneni set id_opravneni_typ_pristupu_budova = 2 where id_opravneni = 'XXOP0000000001';

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 2, cas_zmn = now(), zmenu_provedl = 'pgadmin';
