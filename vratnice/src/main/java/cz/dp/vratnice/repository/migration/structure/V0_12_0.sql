CREATE TABLE vratnice.navstevni_listek_stav (
    id_navstevni_listek_stav integer NOT NULL,
    nazev_resx character varying(100) NOT NULL
);

insert into vratnice.zdrojovy_text (hash, text) values ('NAVSTEVNI_LISTEK_STAV_PROBEHLA','Proběhla');
insert into vratnice.navstevni_listek_stav (id_navstevni_listek_stav, nazev_resx) values (1, 'NAVSTEVNI_LISTEK_STAV_PROBEHLA');

insert into vratnice.zdrojovy_text (hash, text) values ('NAVSTEVNI_LISTEK_STAV_NEPROBEHLA','Neproběhla');
insert into vratnice.navstevni_listek_stav (id_navstevni_listek_stav, nazev_resx) values (2, 'NAVSTEVNI_LISTEK_STAV_NEPROBEHLA');

insert into vratnice.zdrojovy_text (hash, text) values ('NAVSTEVNI_LISTEK_STAV_KE_ZPRACOVANI','Ke zpracování');
insert into vratnice.navstevni_listek_stav (id_navstevni_listek_stav, nazev_resx) values (3, 'NAVSTEVNI_LISTEK_STAV_KE_ZPRACOVANI');
