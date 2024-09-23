-- verejne - zda je lokalita určena pro výběr
ALTER TABLE vratnice.lokalita ADD verejne boolean not null default true;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 5, cas_zmn = now(), zmenu_provedl = 'pgadmin';