CREATE SEQUENCE vratnice.seq_jmeno_korektura_id_jmeno_korektura INCREMENT 1 START 1;

CREATE TABLE vratnice.jmeno_korektura (
    id_jmeno_korektura VARCHAR(14) NOT NULL,
    jmeno_vstup VARCHAR(50) NOT NULL,
    korektura VARCHAR(50) NOT NULL,
    CONSTRAINT pk_jmeno_korektura  PRIMARY KEY (id_jmeno_korektura)
);



-- Přídání JmenaKorektura
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000001', 'ONDREJ', 'Ondřej');
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000002', 'JINDRICH', 'Jindřich');
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000003', 'LUKAS', 'Lukáš');
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000004', 'FRANTISEK', 'František');
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000005', 'ELISKA', 'Eliška');
insert into vratnice.jmeno_korektura(id_jmeno_korektura, jmeno_vstup, korektura) values ('XXJK0000000006', 'BOZENA', 'Božena');