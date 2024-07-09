create table vratnice.klic_typ (
     id_klic_typ INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_klic_typ primary key (id_klic_typ)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_PRIDELENY','přidělený');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (1, 'KLIC_PRIDELENY');
insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_ZASTUP','zástup');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (2, 'KLIC_ZASTUP');
insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_ZALOZNI','záložní');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (3, 'KLIC_ZALOZNI');
insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_OSTRAHA','ostraha');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (4, 'KLIC_OSTRAHA');
insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_TREZOR','trezor');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (5, 'KLIC_TREZOR');
insert into vratnice.zdrojovy_text (hash, text) values ('KLIC_UKLID','úklid');
insert into vratnice.klic_typ (id_klic_typ, nazev_resx) values (6, 'KLIC_UKLID');

ALTER TABLE vratnice.klic 
    DROP COLUMN typ_klice;


ALTER TABLE vratnice.klic
    ADD COLUMN id_klic_typ INTEGER;

 ALTER TABLE vratnice.klic ADD CONSTRAINT fk_klic_id_klic_typ
	FOREIGN KEY (id_klic_typ) REFERENCES vratnice.klic_typ (id_klic_typ) ON DELETE No Action ON UPDATE No Action