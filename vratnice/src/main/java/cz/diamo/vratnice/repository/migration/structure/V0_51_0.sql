-- Tabulka uživatel - závod (pro možný přístup k ostatním závodům)
create table vratnice.uzivatel_navstevni_listek_typ (
     id_uzivatel VARCHAR(14) not null,
     id_navstevni_listek_typ INTEGER not null,
     constraint pk_uzivatel_navstevni_listek_typ primary key (id_uzivatel, id_navstevni_listek_typ)   
);

ALTER TABLE vratnice.uzivatel_navstevni_listek_typ ADD CONSTRAINT fk_uzivatel_navstevni_listek_typ_id_uzivatel
	FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE vratnice.uzivatel_navstevni_listek_typ ADD CONSTRAINT fk_uzivatel_navstevni_listek_typ_id_navstevni_listek_typ
	FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ  (id_navstevni_listek_typ) ON DELETE No Action ON UPDATE No Action
;

insert into vratnice.uzivatel_navstevni_listek_typ (id_uzivatel, id_navstevni_listek_typ) values ('TKUZ0000000001', 1);
insert into vratnice.uzivatel_navstevni_listek_typ (id_uzivatel, id_navstevni_listek_typ) values ('TKUZ0000000002', 2);
insert into vratnice.uzivatel_navstevni_listek_typ (id_uzivatel, id_navstevni_listek_typ) values ('TKUZ0000000003', 1);
insert into vratnice.uzivatel_navstevni_listek_typ (id_uzivatel, id_navstevni_listek_typ) values ('TKUZ0000000004', 2);
