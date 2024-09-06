update vratnice.lokalita set id_externi = id_lokalita, cas_zmn = now(), zmenu_provedl = 'pgadmin' where id_externi is null;

ALTER TABLE vratnice.lokalita RENAME COLUMN id_externi TO kod;
ALTER TABLE vratnice.lokalita ALTER COLUMN kod TYPE varchar(100) USING kod::varchar(100);
ALTER TABLE vratnice.lokalita ALTER COLUMN kod SET NOT NULL;
ALTER TABLE vratnice.lokalita ALTER COLUMN nazev TYPE varchar(1000) USING nazev::varchar(1000);

-- Číselník typů přístupů k budovám - oprávnění
CREATE TABLE zadosti.opravneni_typ_pristupu_budova (
    id_opravneni_typ_pristupu_budova INTEGER not null,
    nazev_resx VARCHAR(100) not null,
    constraint pk_opravneni_typ_pristupu_budova primary key (id_opravneni_typ_pristupu_budova)            
);

insert into zadosti.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU','Bez přístupu');
insert into zadosti.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (1, 'TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU');
insert into zadosti.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_VSE','Vše');
insert into zadosti.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (2, 'TYP_PRIST_BUDOVA_OPR_VSE');
insert into zadosti.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_VYBER','Výběrem');
insert into zadosti.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (3, 'TYP_PRIST_BUDOVA_OPR_VYBER');
insert into zadosti.zdrojovy_text (hash, text) values ('TYP_PRIST_BUDOVA_OPR_ZAVOD','Dle závodu');
insert into zadosti.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova, nazev_resx) values (4, 'TYP_PRIST_BUDOVA_OPR_ZAVOD');

-- Doplnění typu přístupu na oprávnění
ALTER TABLE zadosti.opravneni ADD id_opravneni_typ_pristupu_budova INTEGER not null default 1;
ALTER TABLE zadosti.opravneni ADD CONSTRAINT fk_opravneni_id_opravneni_typ_pristupu_budova
	FOREIGN KEY (id_opravneni_typ_pristupu_budova) REFERENCES zadosti.opravneni_typ_pristupu_budova (id_opravneni_typ_pristupu_budova) ON DELETE No Action ON UPDATE No Action;

-- Oprávnění - budova
create table zadosti.opravneni_budova (
      id_opravneni VARCHAR(14) not null,
      id_budova VARCHAR(14) not null,
      constraint pk_opravneni_budova primary key (id_opravneni, id_budova)         
);

ALTER TABLE zadosti.opravneni_budova ADD CONSTRAINT fk_opravneni_budova_id_opravneni
	FOREIGN KEY (id_opravneni) REFERENCES zadosti.opravneni (id_opravneni) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE zadosti.opravneni_budova ADD CONSTRAINT fk_opravneni_budova_id_budova
	FOREIGN KEY (id_budova) REFERENCES zadosti.budova (id_budova) ON DELETE No Action ON UPDATE No Action
;

-- Doplnění přístupu k budovám pro oprávnění hlavní administrátor
update zadosti.opravneni set id_opravneni_typ_pristupu_budova = 2 where id_opravneni = 'XXOP0000000001';

-- Verze DB
update vratnice.databaze set verze_db = 0, sub_verze_db = 97, cas_zmn = now(), zmenu_provedl = 'pgadmin';