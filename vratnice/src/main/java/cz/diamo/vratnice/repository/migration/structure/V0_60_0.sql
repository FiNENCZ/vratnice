-- Pro tabulku sluzebni_vozidlo
UPDATE vratnice.sluzebni_vozidlo
SET id_vozidlo_typ = 1;

-- Pro tabulku vjezd_vozidla
UPDATE vratnice.vjezd_vozidla
SET id_vozidlo_typ = 1;

-- Pro tabulku povoleni_vjezdu_vozidla_typ_vozidla
UPDATE vratnice.povoleni_vjezdu_vozidla_typ_vozidla
SET id_vozidlo_typ = 1;