-- #### VYTVOŘENÍ TABULKY KLIC #### --

-- Sekvence pro ID klíče
CREATE SEQUENCE vratnice.seq_klic_id_klic INCREMENT 1 START 1;

-- Vytvoření tabulky klic
CREATE TABLE vratnice.klic (
    id_klic VARCHAR(14) NOT NULL,
    specialni BOOLEAN NOT NULL,
    nazev VARCHAR(50) NOT NULL,
    kod_cipu VARCHAR(50) NOT NULL,
    lokalita VARCHAR(50) NOT NULL,
    budova VARCHAR(50) NOT NULL,
    poschodi INTEGER NOT NULL,
    mistnost VARCHAR(50) NOT NULL,
    typ_klice VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'aktivní',
    CONSTRAINT pk_klic PRIMARY KEY (id_klic)
);

-- Vložení prvních dvou záznamů
INSERT INTO vratnice.klic (id_klic, specialni, nazev, kod_cipu, lokalita, budova, poschodi, mistnost, typ_klice, status) VALUES
('XXKL0000000001', false, 'Key1', 'ChipCode1', 'Location1', 'Building1', 1, 'Room1', 'Type1', 'aktivní'),
('XXKL0000000002', true, 'Key2', 'ChipCode2', 'Location2', 'Building2', 2, 'Room2', 'Type2', 'aktivní');
