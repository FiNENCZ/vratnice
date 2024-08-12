CREATE SEQUENCE vratnice.seq_vratnice_id_vratnice INCREMENT 1 START 1;

CREATE TABLE vratnice.vratnice (
    id_vratnice varchar(14) NOT NULL,
    nazev varchar(50) NOT NULL,
    id_zavod varchar(14) NOT NULL,
    id_lokalita varchar(14) NOT NULL,
    osobni boolean DEFAULT false NOT NULL,
    navstevni boolean DEFAULT false NOT NULL,
    vjezdova boolean DEFAULT false NOT NULL,
    id_navstevni_listek_typ int4 NOT NULL,
    odchozi_turniket boolean DEFAULT false NOT NULL,
    poznamka varchar(4000) NULL,
    aktivita boolean DEFAULT true NOT NULL,
    cas_zmn timestamp(6) NULL,
    zmenu_provedl varchar(100) NULL,
    CONSTRAINT pk_vratnice PRIMARY KEY (id_vratnice),
    CONSTRAINT fk_vratnice_id_zavod FOREIGN KEY (id_zavod) REFERENCES vratnice.zavod(id_zavod) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_vratnice_id_lokalita FOREIGN KEY (id_lokalita) REFERENCES vratnice.lokalita(id_lokalita) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_vratnice_id_navstevni_listek_typ FOREIGN KEY (id_navstevni_listek_typ) REFERENCES vratnice.navstevni_listek_typ(id_navstevni_listek_typ) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX ix_vratnice_id_zavod ON vratnice.vratnice (id_zavod);
CREATE INDEX ix_vratnice_id_lokalita ON vratnice.vratnice (id_lokalita);
CREATE INDEX ix_vratnice_id_navstevni_listek_typ ON vratnice.vratnice (id_navstevni_listek_typ);
