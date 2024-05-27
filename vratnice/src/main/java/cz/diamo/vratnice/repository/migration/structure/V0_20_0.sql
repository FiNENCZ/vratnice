CREATE SEQUENCE vratnice.seq_zadost_klic_id_zadost_klic INCREMENT 1 START 1;

CREATE TABLE vratnice.zadost_klic (
     id_zadost_klic VARCHAR(14) NOT NULL,
     id_klic VARCHAR(14) NOT NULL,
     id_uzivatel VARCHAR(14) NOT NULL,
     stav VARCHAR(30) NOT NULL,
     trvala BOOLEAN DEFAULT true,
     datum_od DATE,
     datum_do DATE,
     CONSTRAINT pk_zadost_klic PRIMARY KEY (id_zadost_klic),
     CONSTRAINT fk_zadost_klic_id_klic FOREIGN KEY (id_klic) REFERENCES vratnice.klic (id_klic) ON DELETE NO ACTION ON UPDATE NO ACTION,
     CONSTRAINT fk_zadost_klic_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_zadost_klic_id_klic ON vratnice.zadost_klic (id_klic);
CREATE INDEX ix_zadost_klic_id_uzivatel ON vratnice.zadost_klic (id_uzivatel);