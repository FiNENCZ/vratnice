CREATE SEQUENCE vratnice.seq_najemnik_navstevnicka_karta_id_najemnik_navstevnicka_karta INCREMENT 1 START 1;

CREATE TABLE vratnice.najemnik_navstevnicka_karta (
    id_najemnik_navstevnicka_karta VARCHAR(14) NOT NULL,
    jmeno VARCHAR(50) NOT NULL,
    prijmeni VARCHAR(50) NOT NULL,
    cislo_op VARCHAR(30) UNIQUE NOT NULL,
    spolecnost VARCHAR(120),
    cislo_najemni_smlouvy VARCHAR(30),
    cislo_karty VARCHAR(30) NOT NULL,
    duvod_vydani TEXT,
    vydano_od DATE,
    vydano_do DATE,

    CONSTRAINT pk_najemnik_navstevnicka_karta PRIMARY KEY (id_najemnik_navstevnicka_karta)
);
