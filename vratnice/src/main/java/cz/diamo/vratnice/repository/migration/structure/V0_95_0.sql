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


-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 95, cas_zmn = now(), zmenu_provedl = 'pgadmin';