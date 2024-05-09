-- Příznak dohody na pracovní pozici
ALTER TABLE vratnice.pracovni_pozice ADD dohoda boolean not null default false;

-- SapId dohodáře
ALTER TABLE vratnice.pracovni_pozice ADD sap_id_dohodar varchar(100);

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 12, cas_zmn = now(), zmenu_provedl = 'pgadmin';