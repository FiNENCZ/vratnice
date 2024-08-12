ALTER TABLE vratnice.uzivatel_vratnice
ADD COLUMN nastavena_vratnice VARCHAR(14);

ALTER TABLE vratnice.uzivatel_vratnice
ADD CONSTRAINT fk_uzivatel_vratnice_nastavena_vratnice
FOREIGN KEY (nastavena_vratnice) REFERENCES vratnice.vratnice(id_vratnice)
ON DELETE NO ACTION ON UPDATE NO ACTION;