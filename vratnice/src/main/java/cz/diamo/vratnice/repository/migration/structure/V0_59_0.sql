create table vratnice.vozidlo_typ (
     id_vozidlo_typ INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_vozidlo_typ primary key (id_vozidlo_typ)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('VOZIDLO_OSOBNI','osobní');
insert into vratnice.vozidlo_typ (id_vozidlo_typ, nazev_resx) values (1, 'VOZIDLO_OSOBNI');
insert into vratnice.zdrojovy_text (hash, text) values ('VOZIDLO_DODAVKA','dodávka');
insert into vratnice.vozidlo_typ (id_vozidlo_typ, nazev_resx) values (2, 'VOZIDLO_DODAVKA');
insert into vratnice.zdrojovy_text (hash, text) values ('VOZIDLO_NAKLADNI','nákladní');
insert into vratnice.vozidlo_typ (id_vozidlo_typ, nazev_resx) values (3, 'VOZIDLO_NAKLADNI');
insert into vratnice.zdrojovy_text (hash, text) values ('VOZIDLO_SPECIALNI','speciální');
insert into vratnice.vozidlo_typ (id_vozidlo_typ, nazev_resx) values (4, 'VOZIDLO_SPECIALNI');
insert into vratnice.zdrojovy_text (hash, text) values ('VOZIDLO_IZS','IZS');
insert into vratnice.vozidlo_typ (id_vozidlo_typ, nazev_resx) values (5, 'VOZIDLO_IZS');

-- Služební vozidlo změna typu
ALTER TABLE vratnice.sluzebni_vozidlo 
    DROP COLUMN typ;

ALTER TABLE vratnice.sluzebni_vozidlo
    ADD COLUMN id_vozidlo_typ INTEGER;

ALTER TABLE vratnice.sluzebni_vozidlo ADD CONSTRAINT fk_sluzebni_vozidlo_id_vozidlo_typ
	FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ (id_vozidlo_typ) ON DELETE No Action ON UPDATE No Action;

-- Vjezd změna typu
ALTER TABLE vratnice.vjezd_vozidla 
    DROP COLUMN typ_vozidla;

ALTER TABLE vratnice.vjezd_vozidla
    ADD COLUMN id_vozidlo_typ INTEGER;

ALTER TABLE vratnice.vjezd_vozidla ADD CONSTRAINT fk_vjezd_vozidla_id_vozidlo_typ
	FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ (id_vozidlo_typ) ON DELETE No Action ON UPDATE No Action;

--povoleni_vjezdu_vozidla_typ_vozidla změna typu
ALTER TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla 
    DROP COLUMN typ_vozidla;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla
    ADD COLUMN id_vozidlo_typ INTEGER;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_typ_vozidla_id_vozidlo_typ
	FOREIGN KEY (id_vozidlo_typ) REFERENCES vratnice.vozidlo_typ (id_vozidlo_typ) ON DELETE No Action ON UPDATE No Action;