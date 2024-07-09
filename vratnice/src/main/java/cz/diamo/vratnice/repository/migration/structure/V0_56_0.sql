CREATE SEQUENCE vratnice.seq_lokalita_id_lokalita INCREMENT 1 START 1;

CREATE TABLE vratnice.lokalita (
    id_lokalita VARCHAR(14) NOT NULL,
    nazev VARCHAR(80) NOT NULL,
    CONSTRAINT pk_lokalita PRIMARY KEY (id_lokalita)
);

CREATE SEQUENCE vratnice.seq_budova_id_budova INCREMENT 1 START 1;

CREATE TABLE vratnice.budova (
    id_budova VARCHAR(14) NOT NULL,
    nazev VARCHAR(80) NOT NULL,
    id_lokalita VARCHAR(14) NOT NULL,
    CONSTRAINT pk_budova PRIMARY KEY (id_budova),
    CONSTRAINT fk_budova_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita (id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_budova_id_lokalita ON vratnice.budova (id_lokalita);


CREATE SEQUENCE vratnice.seq_poschodi_id_poschodi INCREMENT 1 START 1;

CREATE TABLE vratnice.poschodi (
    id_poschodi VARCHAR(14) NOT NULL,
    nazev VARCHAR(80) NOT NULL,
    id_budova VARCHAR(14) NOT NULL,
    CONSTRAINT pk_poschodi  PRIMARY KEY (id_poschodi),
    CONSTRAINT fk_poschodi_id_budova FOREIGN KEY (id_budova) REFERENCES vratnice.budova (id_budova) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_poschodi_id_budova ON vratnice.poschodi (id_budova);

-- Přidání lokalit
insert into vratnice.lokalita(id_lokalita, nazev) values ('XXLK0000000001', 'Lokalita 1');
insert into vratnice.lokalita(id_lokalita, nazev) values ('XXLK0000000002', 'Lokalita 2');
insert into vratnice.lokalita(id_lokalita, nazev) values ('XXLK0000000003', 'Lokalita 3');

-- Přídání budov
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000001', 'Budova 1', 'XXLK0000000001');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000002', 'Budova 2', 'XXLK0000000001');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000003', 'Budova 3', 'XXLK0000000001');

insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000004', 'Budova 4', 'XXLK0000000002');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000005', 'Budova 5', 'XXLK0000000002');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000006', 'Budova 6', 'XXLK0000000002');

insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000007', 'Budova 7', 'XXLK0000000003');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000008', 'Budova 8', 'XXLK0000000003');
insert into vratnice.budova(id_budova, nazev, id_lokalita) values ('XXBK0000000009', 'Budova 9', 'XXLK0000000003');

-- Přídání poschodi
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000001', 'Budova 1', 'XXBK0000000001');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000002', 'Budova 2', 'XXBK0000000001');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000003', 'Budova 3', 'XXBK0000000001');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000004', 'Budova 4', 'XXBK0000000002');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000005', 'Budova 5', 'XXBK0000000002');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000006', 'Budova 6', 'XXBK0000000002');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000007', 'Budova 7', 'XXBK0000000003');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000008', 'Budova 8', 'XXBK0000000003');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000009', 'Budova 9', 'XXBK0000000003');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000010', 'Budova 10', 'XXBK0000000004');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000011', 'Budova 11', 'XXBK0000000004');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000012', 'Budova 12', 'XXBK0000000004');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000013', 'Budova 13', 'XXBK0000000005');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000014', 'Budova 14', 'XXBK0000000005');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000015', 'Budova 15', 'XXBK0000000005');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000016', 'Budova 16', 'XXBK0000000006');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000017', 'Budova 17', 'XXBK0000000006');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000018', 'Budova 18', 'XXBK0000000006');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000019', 'Budova 19', 'XXBK0000000007');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000020', 'Budova 20', 'XXBK0000000007');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000021', 'Budova 21', 'XXBK0000000007');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000022', 'Budova 22', 'XXBK0000000008');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000023', 'Budova 23', 'XXBK0000000008');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000024', 'Budova 24', 'XXBK0000000008');

insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000025', 'Budova 25', 'XXBK0000000009');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000026', 'Budova 26', 'XXBK0000000009');
insert into vratnice.poschodi(id_poschodi, nazev, id_budova) values ('XXPK0000000027', 'Budova 27', 'XXBK0000000009');