CREATE TABLE vratnice.povoleni_vjezdu_vozidla_rz_vozidla (
    id_povoleni_vjezdu_vozidla VARCHAR(255) NOT NULL,
    rz_vozidla VARCHAR(30) NOT NULL,
    CONSTRAINT fk_povoleni_vjezdu_vozidla_rz_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla)
    REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE CASCADE
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_rz_vozidla_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_rz_vozidla (id_povoleni_vjezdu_vozidla);

-- Tabulka pro typ_vozidla
CREATE TABLE vratnice.povoleni_vjezdu_vozidla_typ_vozidla (
    id_povoleni_vjezdu_vozidla VARCHAR(255) NOT NULL,
    typ_vozidla VARCHAR(50) NOT NULL,
    CONSTRAINT fk_povoleni_vjezdu_vozidla_typ_vozidla FOREIGN KEY (id_povoleni_vjezdu_vozidla)
    REFERENCES vratnice.povoleni_vjezdu_vozidla (id_povoleni_vjezdu_vozidla) ON DELETE CASCADE
);

CREATE INDEX ix_povoleni_vjezdu_vozidla_typ_vozidla_id_povoleni_vjezdu_vozidla ON vratnice.povoleni_vjezdu_vozidla_typ_vozidla (id_povoleni_vjezdu_vozidla);

ALTER TABLE vratnice.povoleni_vjezdu_vozidla
DROP COLUMN IF EXISTS rz_vozidla,
DROP COLUMN IF EXISTS typ_vozidla;