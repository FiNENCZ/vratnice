-- Úprava tabulky Lokalita
alter table vratnice.lokalita add column aktivita boolean not null default true;
alter table vratnice.lokalita add column cas_zmn timestamp (6);
alter table vratnice.lokalita add column zmenu_provedl VARCHAR(100);

update vratnice.lokalita set cas_zmn = now(), zmenu_provedl = 'pgadmin';

alter table vratnice.lokalita alter column cas_zmn set not null;

-- Úprava tabulky Budova
alter table vratnice.budova add column aktivita boolean not null default true;
alter table vratnice.budova add column cas_zmn timestamp (6);
alter table vratnice.budova add column zmenu_provedl VARCHAR(100);

update vratnice.budova set cas_zmn = now(), zmenu_provedl = 'pgadmin';

alter table vratnice.budova alter column cas_zmn set not null;

-- Úprava tabulky Poschodi
alter table vratnice.poschodi add column aktivita boolean not null default true;
alter table vratnice.poschodi add column cas_zmn timestamp (6);
alter table vratnice.poschodi add column zmenu_provedl VARCHAR(100);

update vratnice.poschodi set cas_zmn = now(), zmenu_provedl = 'pgadmin';

alter table vratnice.poschodi alter column cas_zmn set not null;

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 68, cas_zmn = now(), zmenu_provedl = 'pgadmin';