create table vratnice.historie_vypujcek_akce (
     id_historie_vypujcek_akce INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_historie_vypujcek_akce primary key (id_historie_vypujcek_akce)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_VYPUJCEK_VYPUJCEN','vypůjčen');
insert into vratnice.historie_vypujcek_akce (id_historie_vypujcek_akce, nazev_resx) values (1, 'HISTORIE_VYPUJCEK_VYPUJCEN');
insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_VYPUJCEK_VRACEN','vrácen');
insert into vratnice.historie_vypujcek_akce (id_historie_vypujcek_akce, nazev_resx) values (2, 'HISTORIE_VYPUJCEK_VRACEN');


ALTER TABLE vratnice.historie_vypujcek 
    DROP COLUMN stav;


ALTER TABLE vratnice.historie_vypujcek
    ADD COLUMN id_historie_vypujcek_akce INTEGER;

ALTER TABLE vratnice.historie_vypujcek ADD CONSTRAINT fk_historie_vypujcek_id_historie_vypujcek_akce
	FOREIGN KEY (id_historie_vypujcek_akce) REFERENCES vratnice.historie_vypujcek_akce (id_historie_vypujcek_akce) ON DELETE No Action ON UPDATE No Action;


UPDATE vratnice.historie_vypujcek
    SET id_historie_vypujcek_akce = 1;