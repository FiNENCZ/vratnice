-- Tabulka pro žádost
create table vratnice.zadost_externi (
    id_zadost_externi VARCHAR(14) not null,
    id_uzivatel VARCHAR(14) not null,
    cas timestamp (6) not null,
    typ VARCHAR(4000) not null,
    id_uzivatel_vytvoril VARCHAR(14),
    datum_predani timestamp (6),
    poznamka VARCHAR(4000),
    aktivita smallint not null default 1, 
    cas_zmn timestamp (6) not null,
    zmenu_provedl VARCHAR(100), 
    constraint pk_zadost_externi primary key (id_zadost_externi)   
);

ALTER TABLE vratnice.zadost_externi ADD CONSTRAINT fk_zadost_externi_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.zadost_externi ADD CONSTRAINT fk_zadost_externi_id_uzivatel_vytvoril
	FOREIGN KEY (id_uzivatel_vytvoril) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

-- Tabulka vazby žádost-záznam
create table vratnice.zadost_externi_zaznam (
    id_zadost_externi VARCHAR(14) not null,
    id_zaznam VARCHAR(14) not null,
    constraint pk_zadost_externi_zaznam primary key (id_zadost_externi, id_zaznam)   
);

ALTER TABLE vratnice.zadost_externi_zaznam ADD CONSTRAINT fk_zadost_externi_zaznam_id_zadost_externi
	FOREIGN KEY (id_zadost_externi) REFERENCES vratnice.zadost_externi (id_zadost_externi) ON DELETE No Action ON UPDATE No Action
;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 9, cas_zmn = now(), zmenu_provedl = 'pgadmin';