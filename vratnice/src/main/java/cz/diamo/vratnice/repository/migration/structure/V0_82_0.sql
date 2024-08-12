create table vratnice.uzivatel_vsechny_vratnice (
     id_uzivatel VARCHAR(14) not null,
     aktivni_vsechny_vratnice BOOLEAN not null,
     constraint pk_uzivatel_vsechny_vratnice primary key (id_uzivatel)   
);

ALTER TABLE vratnice.uzivatel_vsechny_vratnice ADD CONSTRAINT fk_uzivatel_vsechny_vratnice_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;
