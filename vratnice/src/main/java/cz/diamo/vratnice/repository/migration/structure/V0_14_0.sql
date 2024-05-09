-- Role pro testování nových věcí
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_TESTER','Tester - přístup k nově vytvořeným funkcím aplikace');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_TESTER', 'ROLE_TESTER');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 14, cas_zmn = now(), zmenu_provedl = 'pgadmin';