-- Doplněn příznak externího uživatele
ALTER TABLE vratnice.uzivatel ADD externi boolean not null default false;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 94, cas_zmn = now(), zmenu_provedl = 'pgadmin';