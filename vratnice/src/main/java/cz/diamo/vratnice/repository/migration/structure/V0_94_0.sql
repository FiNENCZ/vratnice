-- Přejmenování správy externích uživatelů na správu externích systémů
update vratnice.zdrojovy_text set text = 'Správa externích systémů' where hash = 'ROLE_SPRAVA_EXTERNICH_UZIVATELU';

-- Doplněn příznak externího uživatele
ALTER TABLE vratnice.uzivatel ADD externi boolean not null default false;

-- Nová role pro správu vytváření nových externích uživatelů
insert into vratnice.zdrojovy_text (hash, text) values ('ROLE_SPRAVA_UZIVATELU_EXT','Správce externích uživatelů');
insert into vratnice.role (authority, nazev_resx) values ('ROLE_SPRAVA_UZIVATELU_EXT', 'ROLE_SPRAVA_UZIVATELU_EXT');


-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 94, cas_zmn = now(), zmenu_provedl = 'pgadmin';