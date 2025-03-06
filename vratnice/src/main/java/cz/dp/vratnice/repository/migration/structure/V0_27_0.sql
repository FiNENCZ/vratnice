INSERT INTO vratnice.uzivatel (
    id_uzivatel, 
    id_zavod, 
    sap_id, 
    jmeno, 
    prijmeni, 
    email,
    nazev, 
    datum_od, 
    cas_zmn, 
    aktivita, 
    ukonceno, 
    pruzna_prac_doba, 
    externi
) VALUES 
    ('XXUZ0000000001', 'XXZA0000000001', '1', 'Jan', 'Novák', 'jan.novak@email.cz', 'Jan Novák', '2025-03-06', CURRENT_TIMESTAMP, true, false, false, false),
    ('XXUZ0000000002', 'XXZA0000000001', '2', 'Marie', 'Svobodová', 'marie.svobodova@email.cz', 'Marie Svobodová', '2025-03-06', CURRENT_TIMESTAMP, true, false, true, false),
    ('XXUZ0000000003', 'XXZA0000000001', '3', 'Petr', 'Dvořák', 'petr.dvorak@email.cz', 'Petr Dvořák', '2025-03-06', CURRENT_TIMESTAMP, true, false, false, false),
    ('XXUZ0000000004', 'XXZA0000000001', '4', 'Kateřina', 'Kovářová', 'katerina.kovarova@email.cz', 'Kateřina Kovářová', '2025-03-06' ,CURRENT_TIMESTAMP, true, false, true, false);