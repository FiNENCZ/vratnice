-- Zakládací skript pro vytvoření databáze
-- Spustit jako uživatel postgres nad databází postgres

create user vratnice with ENCRYPTED password '*****';

CREATE DATABASE vratnicedb
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'Czech_Czechia.1250'
    LC_CTYPE = 'Czech_Czechia.1250'
    CONNECTION LIMIT = -1
    template template0;

grant all privileges on database vratnicedb to vratnice ;


CREATE SCHEMA vratnice
    AUTHORIZATION vratnice;