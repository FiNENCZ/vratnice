-- Přídání číselníku stav
create table vratnice.sluzebni_vozidlo_stav (
    id_sluzebni_vozidlo_stav INTEGER not null,
    nazev_resx VARCHAR(100) not null,
    constraint pk_sluzebni_vozidlo_stav primary key (id_sluzebni_vozidlo_stav)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_STAV_AKTIVNI','aktivní');
insert into vratnice.sluzebni_vozidlo_stav (id_sluzebni_vozidlo_stav, nazev_resx) values (1, 'SLUZEBNI_VOZIDLO_STAV_AKTIVNI');
insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_STAV_BLOKOVANE','blokované');
insert into vratnice.sluzebni_vozidlo_stav (id_sluzebni_vozidlo_stav, nazev_resx) values (2, 'SLUZEBNI_VOZIDLO_STAV_BLOKOVANE');

ALTER TABLE vratnice.sluzebni_vozidlo 
    DROP COLUMN stav;


ALTER TABLE vratnice.sluzebni_vozidlo
    ADD COLUMN id_sluzebni_vozidlo_stav INTEGER;

ALTER TABLE vratnice.sluzebni_vozidlo ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_stav
	FOREIGN KEY (id_sluzebni_vozidlo_stav) REFERENCES vratnice.sluzebni_vozidlo_stav (id_sluzebni_vozidlo_stav) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.sluzebni_vozidlo
    SET id_sluzebni_vozidlo_stav = 1;