ALTER TABLE vratnice.historie_vypujcek
ADD COLUMN id_vratny VARCHAR(14),
ADD CONSTRAINT fk_historie_vypujcek_id_vratny FOREIGN KEY (id_vratny) REFERENCES vratnice.uzivatel (id_uzivatel) ON DELETE NO ACTION ON UPDATE NO ACTION;
