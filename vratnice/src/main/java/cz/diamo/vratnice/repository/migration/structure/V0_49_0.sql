-- Vytvoření tabulky navstevni_listek_uzivatel pro Many-to-Many vztah s entitou Uzivatel
CREATE TABLE IF NOT EXISTS vratnice.navstevni_listek_uzivatel (
    id_navstevni_listek VARCHAR(14) NOT NULL,
    id_uzivatel VARCHAR(14) NOT NULL,
    CONSTRAINT pk_navstevni_listek_uzivatel  PRIMARY KEY (id_navstevni_listek, id_uzivatel ),
    CONSTRAINT fk_navstevni_listek_uzivatel_id_navstevni_listek FOREIGN KEY (id_navstevni_listek) REFERENCES vratnice.navstevni_listek (id_navstevni_listek) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_navstevni_listek_uzivatel_id_uzivatel FOREIGN KEY (id_uzivatel) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX ix_navstevni_listek_uzivatel_id_navstevni_listek ON vratnice.navstevni_listek_uzivatel (id_navstevni_listek);
CREATE INDEX ix_navstevni_listek_uzivatel_id_uzivatel ON vratnice.navstevni_listek_uzivatel (id_uzivatel);