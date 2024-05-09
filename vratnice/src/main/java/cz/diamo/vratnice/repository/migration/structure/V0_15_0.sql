-- Uprava tabulky uzivatelu
alter table vratnice.uzivatel
    add column id_zakazka varchar(14);

ALTER TABLE vratnice.uzivatel ADD CONSTRAINT fk_uzivatel_id_zakazka
	FOREIGN KEY (id_zakazka) REFERENCES vratnice.zakazka (id_zakazka) ON DELETE No Action ON UPDATE No Action;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 15, cas_zmn = now(), zmenu_provedl = 'pgadmin';