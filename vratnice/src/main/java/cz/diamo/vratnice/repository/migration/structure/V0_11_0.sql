-- Do kmenových dat doplněn čip
ALTER TABLE vratnice.kmenova_data ADD cip_1 VARCHAR(100);
ALTER TABLE vratnice.kmenova_data ADD cip_2 VARCHAR(100);

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 11, cas_zmn = now(), zmenu_provedl = 'pgadmin';