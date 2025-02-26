---Úprava tabulky povoleni_vjezdu_vozidla

-- Odstranění sloupce stav
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    DROP COLUMN stav;

-- Přidání nového sloupce id_zadost_stav a s cizím klíčem
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN id_zadost_stav INTEGER NOT NULL;


ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_zadost_stav FOREIGN KEY (id_zadost_stav) REFERENCES vratnice.zadost_stav(id_zadost_stav);

CREATE INDEX ix_povoleni_vjezdu_vozidla_id_zadost_stav ON vratnice.povoleni_vjezdu_vozidla USING btree (id_zadost_stav);

-- Přidání dalších sloupců
ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN poznamka varchar(4000) NULL,
    ADD COLUMN aktivita boolean DEFAULT true NOT NULL,
    ADD COLUMN cas_zmn timestamp(6) NULL,
    ADD COLUMN zmenu_provedl varchar(100) NULL;
