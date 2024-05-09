-- Přidání nové role
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_UZIVATELU','Správce uživatelů');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_UZIVATELU', 'ROLE_SPRAVA_UZIVATELU');

insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_SPRAVA_UZIVATELU');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 6, cas_zmn = now(), zmenu_provedl = 'pgadmin';