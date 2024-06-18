ALTER TABLE vratnice.historie_sluzebni_vozidlo
DROP COLUMN aktivita;


ALTER TABLE vratnice.sluzebni_vozidlo
ADD COLUMN aktivita BOOLEAN NOT NULL DEFAULT true;
