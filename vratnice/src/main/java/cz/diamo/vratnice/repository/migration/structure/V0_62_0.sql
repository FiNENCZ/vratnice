UPDATE vratnice.sluzebni_vozidlo
SET id_sluzebni_vozidlo_kategorie = 1;

-- Přídání číselníku funkce
create table vratnice.sluzebni_vozidlo_funkce (
     id_sluzebni_vozidlo_funkce INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_sluzebni_vozidlo_funkce primary key (id_sluzebni_vozidlo_funkce)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_FUNKCE_REDITEL','ředitel');
insert into vratnice.sluzebni_vozidlo_funkce (id_sluzebni_vozidlo_funkce, nazev_resx) values (1, 'SLUZEBNI_VOZIDLO_FUNKCE_REDITEL');
insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK','náměstek');
insert into vratnice.sluzebni_vozidlo_funkce (id_sluzebni_vozidlo_funkce, nazev_resx) values (2, 'SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK');

ALTER TABLE vratnice.sluzebni_vozidlo 
    DROP COLUMN funkce;


ALTER TABLE vratnice.sluzebni_vozidlo
    ADD COLUMN id_sluzebni_vozidlo_funkce INTEGER;

ALTER TABLE vratnice.sluzebni_vozidlo ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_funkce
	FOREIGN KEY (id_sluzebni_vozidlo_funkce) REFERENCES vratnice.sluzebni_vozidlo_funkce (id_sluzebni_vozidlo_funkce) ON DELETE No Action ON UPDATE No Action