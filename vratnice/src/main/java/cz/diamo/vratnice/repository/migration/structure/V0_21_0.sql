CREATE SEQUENCE vratnice.seq_historie_vypujcek_id_historie_vypujcek INCREMENT 1 START 1;

CREATE TABLE vratnice.historie_vypujcek (
    id_historie_vypujcek VARCHAR(14) NOT NULL,
    id_zadost_klic VARCHAR(14) NOT NULL,
    stav VARCHAR(30) NOT NULL,
    datum DATE,
    CONSTRAINT pk_historie_vypujcek PRIMARY KEY (id_historie_vypujcek),
    CONSTRAINT fk_historie_vypujcek_id_zadost_klic FOREIGN KEY (id_zadost_klic) REFERENCES vratnice.zadost_klic (id_zadost_klic) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_historie_vypujcek_id_zadost_klic ON vratnice.historie_vypujcek (id_zadost_klic);
