-- Tabulka typů stravy
create table vratnice.navstevni_listek_typ (
     id_navstevni_listek_typ INTEGER not null,
     nazev_resx VARCHAR(100) not null,
    constraint pk_navstevni_listek_typ primary key (id_navstevni_listek_typ)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('NAVSTEVNI_LISTEK_ELEKTRONICKY','Elektronický');
insert into vratnice.navstevni_listek_typ (id_navstevni_listek_typ, nazev_resx) values (1, 'NAVSTEVNI_LISTEK_ELEKTRONICKY');
insert into vratnice.zdrojovy_text (hash, text) values ('NAVSTEVNI_LISTEK_PAPIROVY','Papírový');
insert into vratnice.navstevni_listek_typ (id_navstevni_listek_typ, nazev_resx) values (2, 'NAVSTEVNI_LISTEK_PAPIROVY');

ALTER TABLE vratnice.navstevni_listek
    ADD COLUMN id_navstevni_listek_typ INTEGER;
    
ALTER TABLE vratnice.navstevni_listek ADD CONSTRAINT fk_navstevni_listek_id_navstevni_listek_typ
	FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ (id_navstevni_listek_typ) ON DELETE No Action ON UPDATE No Action
;
