update vratnice.lokalita set id_externi = id_lokalita, cas_zmn = now(), zmenu_provedl = 'pgadmin' where id_externi is null;

ALTER TABLE vratnice.lokalita RENAME COLUMN id_externi TO kod;
ALTER TABLE vratnice.lokalita ALTER COLUMN kod TYPE varchar(100) USING kod::varchar(100);
ALTER TABLE vratnice.lokalita ALTER COLUMN kod SET NOT NULL;
ALTER TABLE vratnice.lokalita ALTER COLUMN nazev TYPE varchar(1000) USING nazev::varchar(1000);

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 96, cas_zmn = now(), zmenu_provedl = 'pgadmin';