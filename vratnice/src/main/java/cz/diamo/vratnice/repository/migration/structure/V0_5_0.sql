-- Přidání nové role
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_CISELNIKY_ZAKAZKY','Správce zakázek');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_CISELNIKY_ZAKAZKY', 'ROLE_CISELNIKY_ZAKAZKY');

insert into vratnice.opravneni_role(id_opravneni, authority) values ('XXOP0000000001', 'ROLE_CISELNIKY_ZAKAZKY');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 5, cas_zmn = now(), zmenu_provedl = 'pgadmin';