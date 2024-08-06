ALTER TABLE vratnice.historie_klic
    ADD COLUMN duvod varchar(4000) NULL;

insert into vratnice.zdrojovy_text (hash, text) values ('HISTORIE_KLIC_AKCE_VYMENA','výměna');
insert into vratnice.historie_klic_akce (id_historie_klic_akce, nazev_resx) values (6, 'HISTORIE_KLIC_AKCE_VYMENA');