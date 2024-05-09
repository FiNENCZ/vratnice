CREATE SEQUENCE vratnice.seq_pracovni_pozice_log_id_pracovni_pozice_log INCREMENT 1 START 1;
create table vratnice.pracovni_pozice_log (
    id_pracovni_pozice_log VARCHAR(14) not null,   
    cas_volani timestamp (6) not null,
    cas_zpracovani timestamp (6),
    chyba text,
    pocet_zaznamu integer not null default 0,
    ok boolean not null default false,
    json_log text,
    poznamka VARCHAR(4000),
    aktivita boolean not null default true, 
    cas_zmn timestamp (6) not null,
    zmenu_provedl VARCHAR(100), 
    constraint pk_pracovni_pozice_log primary key (id_pracovni_pozice_log)   
);

-- Nová role pro zobrazení logů
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SERVIS_ORG_STR','Servis - organizační struktura');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SERVIS_ORG_STR', 'ROLE_SERVIS_ORG_STR');

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 18, cas_zmn = now(), zmenu_provedl = 'pgadmin';