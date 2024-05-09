-- Změna aktivity z int na bool
ALTER TABLE vratnice.zadost_externi ALTER COLUMN aktivita DROP DEFAULT;
ALTER TABLE vratnice.zadost_externi ALTER aktivita TYPE bool USING CASE WHEN aktivita=0 THEN FALSE ELSE TRUE END;
ALTER TABLE vratnice.zadost_externi ALTER COLUMN aktivita SET DEFAULT TRUE;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 10, cas_zmn = now(), zmenu_provedl = 'pgadmin';