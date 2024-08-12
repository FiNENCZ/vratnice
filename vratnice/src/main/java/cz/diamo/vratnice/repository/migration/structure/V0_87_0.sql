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