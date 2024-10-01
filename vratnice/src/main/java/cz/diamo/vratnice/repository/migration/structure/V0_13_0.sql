ALTER TABLE vratnice.navstevni_listek_stav
ADD CONSTRAINT pk_navstevni_listek_stav PRIMARY KEY (id_navstevni_listek_stav);


CREATE TABLE vratnice.navsteva_uzivatel_stav (
    id_navsteva_uzivatel_stav varchar(14) NOT NULL,
    id_navstevni_listek varchar(14) NOT NULL,       
    id_uzivatel varchar(14) NOT NULL,              
    id_navstevni_listek_stav int4 NOT NULL,         
    CONSTRAINT pk_navsteva_uzivatel_stav PRIMARY KEY (id_navsteva_uzivatel_stav),
    CONSTRAINT fk_navsteva_uzivatel_stav_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek(id_navstevni_listek),
    CONSTRAINT fk_navsteva_uzivatel_stav_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel(id_uzivatel),
    CONSTRAINT fk_navsteva_uzivatel_stav_id_navstevni_listek_stav FOREIGN KEY (id_navstevni_listek_stav) REFERENCES vratnice.navstevni_listek_stav(id_navstevni_listek_stav)
);

CREATE INDEX ix_navsteva_uzivatel_stav_id_navstevni_listek ON vratnice.navsteva_uzivatel_stav USING btree (id_navstevni_listek);
CREATE INDEX ix_navsteva_uzivatel_stav_id_uzivatel ON vratnice.navsteva_uzivatel_stav USING btree (id_uzivatel);


DROP INDEX IF EXISTS vratnice.ix_navstevni_listek_uzivatel_id_navstevni_listek;
DROP INDEX IF EXISTS vratnice.ix_navstevni_listek_uzivatel_id_uzivatel;

DROP TABLE IF EXISTS vratnice.navstevni_listek_uzivatel;

CREATE TABLE vratnice.navstevni_listek_uzivatel_stav (
    id_navstevni_listek varchar(14) NOT NULL,
    id_navsteva_uzivatel_stav varchar(14) NOT NULL,
    CONSTRAINT pk_navstevni_listek_uzivatel_stav PRIMARY KEY (id_navstevni_listek, id_navsteva_uzivatel_stav),
    CONSTRAINT fk_navstevni_listek_uzivatel_stav_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek(id_navstevni_listek) ON DELETE CASCADE,
    CONSTRAINT fk_navstevni_listek_uzivatel_stav_navsteva_uzivatel_stav FOREIGN KEY (id_navsteva_uzivatel_stav) REFERENCES vratnice.navsteva_uzivatel_stav(id_navsteva_uzivatel_stav) ON DELETE CASCADE
);

CREATE INDEX ix_navstevni_listek_uzivatel_stav_navstevni_listek ON vratnice.navstevni_listek_uzivatel_stav USING btree (id_navstevni_listek);
CREATE INDEX ix_navstevni_listek_uzivatel_stav_navsteva_uzivatel_stav ON vratnice.navstevni_listek_uzivatel_stav USING btree (id_navsteva_uzivatel_stav);

