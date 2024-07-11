create table vratnice.sluzebni_vozidlo_kategorie (
     id_sluzebni_vozidlo_kategorie INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_sluzebni_vozidlo_kategorie primary key (id_sluzebni_vozidlo_kategorie)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE','referentské');
insert into vratnice.sluzebni_vozidlo_kategorie (id_sluzebni_vozidlo_kategorie, nazev_resx) values (1, 'SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE');
insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE','manažerské');
insert into vratnice.sluzebni_vozidlo_kategorie (id_sluzebni_vozidlo_kategorie, nazev_resx) values (2, 'SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE');
insert into vratnice.zdrojovy_text (hash, text) values ('SLUZEBNI_VOZIDLO_KATEGORIE_JINE','jiné');
insert into vratnice.sluzebni_vozidlo_kategorie (id_sluzebni_vozidlo_kategorie, nazev_resx) values (3, 'SLUZEBNI_VOZIDLO_KATEGORIE_JINE');

ALTER TABLE vratnice.sluzebni_vozidlo 
    DROP COLUMN kategorie;


ALTER TABLE vratnice.sluzebni_vozidlo
    ADD COLUMN id_sluzebni_vozidlo_kategorie INTEGER;

ALTER TABLE vratnice.sluzebni_vozidlo ADD CONSTRAINT fk_sluzebni_vozidlo_id_sluzebni_vozidlo_kategorie
	FOREIGN KEY (id_sluzebni_vozidlo_kategorie) REFERENCES vratnice.sluzebni_vozidlo_kategorie (id_sluzebni_vozidlo_kategorie) ON DELETE No Action ON UPDATE No Action