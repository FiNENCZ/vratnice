ALTER TABLE vratnice.sluzebni_vozidlo
    ADD rz varchar(30) NULL,
    ADD poznamka varchar(4000) NULL,
    ADD cas_zmn timestamp(6) NULL,
    ADD zmenu_provedl varchar(100) NULL;