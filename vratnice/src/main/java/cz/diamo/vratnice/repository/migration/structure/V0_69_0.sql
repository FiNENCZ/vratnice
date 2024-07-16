create table vratnice.historie_sluzebni_vozidlo_akce (
     id_historie_sluzebni_vozidlo_akce INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_historie_sluzebni_vozidlo_akce primary key (id_historie_sluzebni_vozidlo_akce)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO','vytvořeno');
insert into vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce, nazev_resx) values (1, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO','upraveno');
insert into vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce, nazev_resx) values (2, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO','odstraněno');
insert into vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce, nazev_resx) values (3, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO','blokovano');
insert into vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce, nazev_resx) values (4, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO','obnoveno');
insert into vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce, nazev_resx) values (5, 'HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO');


ALTER TABLE vratnice.historie_sluzebni_vozidlo 
    DROP COLUMN akce;


ALTER TABLE vratnice.historie_sluzebni_vozidlo
    ADD COLUMN id_historie_sluzebni_vozidlo_akce INTEGER;

ALTER TABLE vratnice.historie_sluzebni_vozidlo ADD CONSTRAINT fk_historie_sluzebni_vozidlo_id_historie_sluzebni_vozidlo_akce
	FOREIGN KEY (id_historie_sluzebni_vozidlo_akce) REFERENCES vratnice.historie_sluzebni_vozidlo_akce (id_historie_sluzebni_vozidlo_akce) ON DELETE No Action ON UPDATE No Action;


UPDATE vratnice.historie_sluzebni_vozidlo
    SET id_historie_sluzebni_vozidlo_akce = 1;