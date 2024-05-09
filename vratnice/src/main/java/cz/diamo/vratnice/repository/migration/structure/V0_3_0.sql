-- Změna hesla externího uživatele
update vratnice.externi_uzivatel set password = '$2a$12$MHjASjXf51sJjKLkuOANt.t6Os8G163CQ40/A6DmUEuZRObM5ZPZ6' where id_externi_uzivatel = 'XXEU0000000001';

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 3, cas_zmn = now(), zmenu_provedl = 'pgadmin';