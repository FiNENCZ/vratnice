--Vymazání nepotřebné role
DELETE FROM vratnice.zdrojovy_text WHERE id_zdrojovy_text = 135;
DELETE FROM vratnice.role WHERE authority = 'ROLE_SPRAVA_DOCHAZKA';


insert into vratnice.zdrojovy_text (hash, text) values ('EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE','Ruční záznamy snímače');
insert into vratnice.role (authority, nazev_resx) values ('EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE', 'EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE');