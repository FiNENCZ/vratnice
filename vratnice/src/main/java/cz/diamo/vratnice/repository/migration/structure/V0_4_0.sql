-- Přidání vazby uživatel-modul
CREATE TABLE vratnice.uzivatel_modul (
	id_uzivatel varchar(14) NOT NULL,
    modul varchar(4000) NOT NULL,
	constraint pk_uzivatel_modul primary key (id_uzivatel, modul)         
);

ALTER TABLE vratnice.uzivatel_modul ADD CONSTRAINT fk_uzivatel_modul_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 4, cas_zmn = now(), zmenu_provedl = 'pgadmin';